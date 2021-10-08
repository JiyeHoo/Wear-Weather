# 描述
Wear OS手表天气，通过高德LBS、和风SDK实现天气预报和生活建议。
更多细节见我的博客：[http://www.jiyehoo.com][1]

# 效果
（加载不出请挂梯，或转博客）

![pic_1][2]

# 第三方库

 - 网络请求:com.squareup.okhttp3:okhttp:4.9.0
 - 数据解析:com.google.code.gson:gson:2.8.6
 - 图片加载:com.github.bumptech.glide:glide:4.11.0
 - 翻页效果:androidx.viewpager2:viewpager2:1.0.0
 - ...

# 说明

 1. 定位通过高德LBS获取的经纬度，需要换成自己的KEY。
 2. 天气数据来自和风天气，需要换成自己的KEY。
 3. 本项目遵循MIT开源协议，但不包括所使用的第三方库。仅供学习使用，请勿用于商业用途。

  [1]: http://blog.jiyehoo.com:81/index.php/archives/251/
  [2]: http://tc.jiyehoo.com:81/images/2021/02/05/2426015752.gif
