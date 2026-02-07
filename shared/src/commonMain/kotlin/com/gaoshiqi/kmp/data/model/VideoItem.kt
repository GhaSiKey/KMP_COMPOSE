package com.gaoshiqi.kmp.data.model

/**
 * 视频数据模型
 *
 * @param id 唯一标识
 * @param title 视频标题
 * @param url 视频播放地址（支持 MP4 直链和 HLS m3u8 流）
 * @param thumbnailUrl 缩略图地址（可选）
 */
data class VideoItem(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String? = null
)

/**
 * 测试视频数据
 *
 * 包含 MP4 和 HLS 格式的示例视频
 */
object VideoData {
    val samples = listOf(
        VideoItem(
            id = "1",
            title = "Big Buck Bunny",
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
        ),
        VideoItem(
            id = "2",
            title = "Elephant Dream",
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
        ),
        VideoItem(
            id = "3",
            title = "HLS 测试流 (Apple)",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8",
            thumbnailUrl = null
        ),
        VideoItem(
            id = "4",
            title = "Sintel Trailer",
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg"
        )
    )

    fun findById(id: String): VideoItem? = samples.find { it.id == id }
}
