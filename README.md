### xserver项目介绍：


#### xserver项目是一个简化xml接口开发的小工具，开发者配置好xml模板之后，可以通过操作hashMap来解析、处理xml报文的功能，屏蔽了对socket的操作，简化开发，提高开发效率，支持一层循环域。

### 与xcomm不同：

#### xcomm是消费方组件，xserver是服务方组件；xcomm主动发起请求，xserver监听socket端口，接收消息，处理、响应请求，同时xserver通过采用线程池来提高响应速度。