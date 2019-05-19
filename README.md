# Welfare 
`我要发·518` 福彩`3D`预测服务作为一款开源的软件预测软件，由 `Kylin` 开发和维护，用户可以免费使用，但作者不对使用者的任何行为负责。

软件基于`spring-boot`框架开发，使用maven管理依赖，在使用过程中有任何问题，欢迎通过`gitcoment`沟通，或者反馈至 shallotsh#gmail.com。

# jar包独立运行

```$xslt
jar -jar ${packagename}.jar
```

# docker中运行 

## 打包
在工程目录下执行：

```$xslt
 mvn package docker:build
``` 

## 运行

```$xslt
sudo docker run -d -it -p 9090:9090  kylin/welfare
```

本工程基于 [Kylin](https://github.com/shallotsh/kylin) 构建。
