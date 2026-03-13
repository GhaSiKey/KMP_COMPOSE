package com.gaoshiqi.kmp.ui.lenticular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaoshiqi.kmp.shared.lenticular.LenticularConfig
import com.gaoshiqi.kmp.shared.lenticular.LenticularEngine
import com.gaoshiqi.kmp.shared.lenticular.SensingAxis
import com.gaoshiqi.kmp.shared.lenticular.TiltSensor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 光栅卡 ViewModel
 *
 * 管理光栅卡功能的全部状态和业务逻辑，包括：
 * - 图片管理（添加、删除、排序）
 * - 感应轴向和灵敏度配置
 * - 传感器数据采集与低通滤波
 * - 通过 LenticularEngine 计算渲染状态
 *
 * @param tiltSensor 倾斜传感器，用于采集设备倾斜角度
 * @param engine 光栅引擎，用于角度→图片映射计算
 */
class LenticularViewModel(
    private val tiltSensor: TiltSensor,
    private val engine: LenticularEngine = LenticularEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LenticularUiState())
    val uiState: StateFlow<LenticularUiState> = _uiState.asStateFlow()

    /** 传感器采集协程 */
    private var sensingJob: Job? = null

    /** 上一次滤波后的 pitch/roll 值 */
    private var previousFilteredPitch: Float = 0f
    private var previousFilteredRoll: Float = 0f

    init {
        // 检测传感器可用性
        _uiState.update { it.copy(isSensorAvailable = tiltSensor.isAvailable()) }
    }

    // ==================== 图片管理 ====================

    /**
     * 添加图片
     *
     * 图片总数不超过 10 张，超出部分会被截断。
     *
     * @param uris 图片 URI 列表
     */
    fun addImages(uris: List<String>) {
        _uiState.update { state ->
            val remaining = MAX_IMAGE_COUNT - state.images.size
            if (remaining <= 0) return@update state
            val toAdd = uris.take(remaining)
            state.copy(images = state.images + toAdd)
        }
    }

    /**
     * 移除指定索引的图片
     *
     * @param index 要移除的图片索引
     */
    fun removeImage(index: Int) {
        _uiState.update { state ->
            if (index !in state.images.indices) return@update state
            state.copy(images = state.images.toMutableList().apply { removeAt(index) })
        }
    }

    /**
     * 移动图片顺序
     *
     * @param fromIndex 原始位置
     * @param toIndex 目标位置
     */
    fun moveImage(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val list = state.images.toMutableList()
            if (fromIndex !in list.indices || toIndex !in list.indices) return@update state
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            state.copy(images = list)
        }
    }

    // ==================== 配置 ====================

    /**
     * 设置感应轴向
     *
     * @param axis 感应轴向（水平/垂直）
     */
    fun setSensingAxis(axis: SensingAxis) {
        _uiState.update { it.copy(sensingAxis = axis) }
    }

    /**
     * 设置每张图片的角度间隔
     *
     * 值会被钳制到 [5°, 30°] 范围内。
     *
     * @param degrees 每张图片对应的角度区间宽度
     */
    fun setDegreesPerImage(degrees: Float) {
        val clamped = degrees.coerceIn(MIN_DEGREES_PER_IMAGE, MAX_DEGREES_PER_IMAGE)
        _uiState.update { it.copy(degreesPerImage = clamped) }
    }

    /**
     * 切换重力感应调试小球的显隐
     */
    fun toggleTiltDebug() {
        _uiState.update { it.copy(showTiltDebug = !it.showTiltDebug) }
    }

    // ==================== 传感器控制 ====================

    /**
     * 开始传感器监听
     *
     * 采集倾斜数据，应用低通滤波，通过 LenticularEngine 计算渲染状态并更新 UI。
     * 如果传感器不可用或图片不足 2 张，不会启动。
     */
    fun startSensing() {
        val state = _uiState.value
        if (!state.isSensorAvailable || !state.canPreview) return
        // 避免重复启动
        if (sensingJob?.isActive == true) return

        previousFilteredPitch = 0f
        previousFilteredRoll = 0f
        engine.reset()

        sensingJob = viewModelScope.launch {
            tiltSensor.startObserving().collect { tiltData ->
                val currentState = _uiState.value

                // 对 pitch 和 roll 分别做低通滤波
                val fPitch = engine.applyLowPassFilter(tiltData.pitch, previousFilteredPitch)
                val fRoll = engine.applyLowPassFilter(tiltData.roll, previousFilteredRoll)
                previousFilteredPitch = fPitch
                previousFilteredRoll = fRoll

                // 根据轴向选择用于光栅引擎计算的角度分量
                val activeAngle = when (currentState.sensingAxis) {
                    SensingAxis.VERTICAL -> fPitch
                    SensingAxis.HORIZONTAL -> fRoll
                }

                // 构建配置并计算渲染状态
                if (currentState.imageCount >= MIN_IMAGE_COUNT) {
                    val config = LenticularConfig(
                        imageCount = currentState.imageCount,
                        sensingAxis = currentState.sensingAxis,
                        degreesPerImage = currentState.degreesPerImage
                    )
                    val renderState = engine.computeRenderState(activeAngle, config)

                    _uiState.update {
                        it.copy(
                            currentAngle = activeAngle,
                            filteredPitch = fPitch,
                            filteredRoll = fRoll,
                            renderState = renderState
                        )
                    }
                }
            }
        }
    }

    /**
     * 停止传感器监听并释放资源
     */
    fun stopSensing() {
        sensingJob?.cancel()
        sensingJob = null
        tiltSensor.stopObserving()
    }

    override fun onCleared() {
        super.onCleared()
        stopSensing()
    }

    companion object {
        /** 最大图片数量 */
        const val MAX_IMAGE_COUNT = 10
        /** 最小图片数量 */
        const val MIN_IMAGE_COUNT = 2
        /** 每张图最小角度间隔 */
        const val MIN_DEGREES_PER_IMAGE = LenticularConfig.MIN_DEGREES_PER_IMAGE
        /** 每张图最大角度间隔 */
        const val MAX_DEGREES_PER_IMAGE = LenticularConfig.MAX_DEGREES_PER_IMAGE
    }
}
