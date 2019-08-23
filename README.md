# 公开课“3D勋章”实现方案
### 												------  opengl es 2.0中加载.obj 与 .mtl

先上一个效果图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823145527893.gif)

看到这个需求，直接反应是`用OpenGLES加载一个.obj(顶点数据)与.mtl(颜色材质信息)文件` 就搞定了（ .obj与.mtl文件由设计师用3dmax、Maya等工具导出）。
本以为是一个简单需求，但做起来发现困难点并不少：

+ OpenGLES如何加载.obj与.mtl文件？
+ 勋章的进入和退出动画效果？
+ 勋章进入和退出过程中的实时光照效果该怎么做？

解决这三个问题，是需求实现的关键。
为此，我设计了以下架构实现方式，先看架构图：

**架构实现：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823145636403.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

我们从下向上说：

+ 最下边运行在Android系统上，这个不用细说
+ .obj 3D文件解析引擎：
自己手动解析的，并不是网上开源框架(大都泛泛而谈，只能做demo) 。如果开源，那`我的这个工程应该就是第一个开源的比较完善的.obj3D文件.mtl材质文件的Java解析库`；
+ 光照系统：
shader 主要用于模拟  环境光、散射光、镜面光(高光)。上线产品，为了效果，必须有光照；
+ 动画引擎：
虽然看起来简单，但设计确是按照一个动画引擎标准设计的；
整个动画引擎可以划分为：场景、层、动画精灵。引擎的架构借鉴了游戏引擎Cocos2d；
这样设计的优点是：
a、若底层不依赖Opengl ES 底层依赖的是View 或者SurfaceView 可直接移植为一个2D动画引擎；
b、继续扩展还可以发展做成骨骼动画；


下面我们对这个架构实现进行详细说明。

## 一、OpenGLES如何加载.obj与.mtl文件？

本来以为网上应该有兼容性较好的obj与mtl的java解析库，但在网上找了好多代码，发现其在加载obj与mtl中，基本都存在较大问题。

网上代码主要分为了以下几个部分：

+ mind3d 2011年就已停止维护(为opengl es1.0)，并且在加载多图形上存在很大的不兼容(**主要解析了obj，mtl没有解析**)。
+ 其他一些obj解析代码，基本都是**解析了obj，不管mtl文件**；
或者简单**解析了mtl，却没有把对应的材质信息应用到opengl 绘制的图形上**。
+ 《Android 3D游戏开发技术宝典——OpenGL ES 2.0》(2012年我和几个同学写的书) 第九章 3D模型加载。 当时写这本书时，也只是简单解析了obj文件，而且对mtl文件并未做解析(网上很多的例子是把这一章的案例直接照搬了)

**mind3d官方地址与源码**：
https://code.google.com/archive/p/min3d/
https://github.com/deadmoose/min3d


###  1.1 Obj 与mtl文件简单举例

obj文件是3D模型文件格式。由Alias|Wavefront公司为3D建模和动画软件”Advanced Visualizer”开发的一种标准，适合用于3D软件模型之间的互导，也可以通过Maya读写。

+ 只支持模型三角面数据和材质信息，无动画功能支持；
+ 其中几何信息由.obj文件提供，材质信息由.mtl文件定义；
+ 文件以行为单位表示一条数据，可以根据行开头的字符判断后续的内容；
+ 其中 # 字符表示注释行

#### 1.1.1 obj文件中主要存放的以下几何信息

+ 三维空间中顶点坐标信息
+ 顶点的纹理坐标(贴图坐标)信息
+ 顶点的法向量信息(计算光照用)

这里我手写了一份triangle.obj文件：

**triangle.obj**
```
# mtl材质文件
# mtllib testvt.mtl

# o 对象名称(Object name)
o adfaf

# 组名称
g default

# 顶点
v 0 0.5 0
v -0.5 -0.5 0
v 0.5 -0.5 0

# 纹理坐标
vt 0.0 1.0
vt 0.0 0.0
vt 1.0 1.0

# 顶点法线
vn 0 0 1

# 当前图元所用材质
usemtl Default

# s Smooth shading across polygons is enabled by smoothing groups.
# Smooth shading can be disabled as well.
s off

# v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(索引起始于1)
f 1/1/1 2/2/1 3/3/1
```

*triangle.obj 增加了详细的注释；*
+ triangle.obj 中规定了模型顶点、纹理、法向量等信息，确定了模型的顶点数据；
 


#### 1.1.2 mtl文件

这里我手写了一份triangle.mtl文件：
mtl中主要规定了几何图形的贴图信息，对环境光、散射光、镜面光的反射情况、透明度等

**triangle.mtl**
```
# 定义一个名为 'Default'的材质
newmtl Default

# 材质的环境光
Ka 0 0 0
# 散射光
Kd 0.784314 0.784314 0.784314
# 镜面光
Ks 0 0 0

# 透明度
d 1

# 为漫反射指定颜色纹理文件
map_Kd test_vt.png
```

*triangle.mtl 增加了详细的注释；*
+ triangle.mtl 中规定了模型材质相关的信息，包括纹理贴图、环境光、镜面光、散射光等相关配置都来自这个文件；

#### 1.1.3 triangle.obj与triangle.mtl加载到OpenGL后的运行效果


![这里写图片描述](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTcwODE0MTAzNzMwMTA3?x-oss-process=image/format,png)

+ 加载triangle.obj与triangle.mtl 模型后的运行效果图如上图所示；
+ 这里为了方便理解，所以仅仅加载的是一个最简单的普通三角形；

![这里写图片描述](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTcwODE0MTAzNzQ2ODU4?x-oss-process=image/format,png)

+ 加载的triangle.obj与triangle.mtl 三角形，在三维空间中的位置如上图所示。
+ 三维空间中顶点的位置信息来源于triangle.obj文件中。

*关于关照与材质相关，详细信息，可参考我的另一篇文章：*
[http://blog.csdn.net/xiaxl/article/details/76826812](http://blog.csdn.net/xiaxl/article/details/76826812)


## 二、动画引擎实现

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823145845625.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

说动画实现之前，先要说一下动画引擎架构的设计。
动画引擎实现，参考了Cocos2d游戏引擎的设计思想，整个动画引擎分为了三层：

### 2.1、动画引擎架构实现

前边说了，这个设计实际参考了Cocos2d游戏引擎的设计思想，所以实现上与Cocos2d有很多的相似之处：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823145905932.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)
+ 场景：场景负责绘制其所包含的全部层；
+ 层：层负责管理、绘制其中的精灵；
层亦可以携带精灵做出一些旋转、平移、缩放 等简单的动画效果；
+ 精灵：精灵是游戏实体，精灵是活泼的，可以做很多的动画效果；
`精灵应该是活泼的、好动的`！！！
而动画引擎中的 `SpriteAnima` ，则是我赋予精灵的动画实现，也是`精灵活泼起来`的根本。

### 2.2、具体的代码实现

在具体分层代码实现上：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823145948571.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

+ 场景：场景继承自`GLSurfaceView` 管理者众多的层，负责页面绘制；
+ 层： 层管理者众多的精灵，并且继承自`SpriteAnima`；
层亦被赋予了动画属性，可以携带众多的精灵完成动画，而不影像精灵的动画；
同时也`赋予了层活泼好动的天性`；
+ 精灵：当然亦继承自`SpriteAnima`，天然活泼好动。

### 2.3、SpriteAnima 动画实现

具体动画类的实现，则参考了`Android属性动画`的实现方案：


**Opengl ES中的属性动画**

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019082315003410.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

以上代码截图，就是我自定义实现的`Opengl ES中的属性动画`。

参考Android属性动画：
+ 通过反射调用，不断更改精灵的AngleY属性值来完成精灵属性的变更。
+ 这里我还加入了Android的`动画差值器 OvershootInterpolator` ，使得勋章动画在运行时，具备`动画越过便边界后，回弹的效果`。我们的勋章精灵变得更加活泼。

**让精灵活泼起来**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150056636.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)
如以上代码截图所示：只要动画尚未结束，则`不断请求GLThread的场景重绘`，来完成`精灵的活泼运动效果`


## 三、3D空间对光的模拟


当光照射到一个物体表面上时，会出现三种情形。 

+ 首先，光可以通过物体表面向空间反射， 产生反射光。 
+ 其次，对于透明体，光可以穿透该物体并从另一端射出，产生透射光。 
+ 最后，部分光将被物体表面吸收而转换成热。 

在上述三部分光中，仅仅是透射光和反射光能够进入人眼产生视觉效果。这里**只考虑被照明物体表面的反射光影响**，假定物体表面光滑不透明且由理想材料构成，环境假设为由白光照明。 
一般来说，**反射光可以分成三个分量，即环境反射、漫反射和镜面反射**。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150118206.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

+ 镜面光：上图中，最亮部分为镜面光
+ 散射光(漫反射光)：上图中，比镜面光稍暗部分为散射光
+ 环境光：上图中，最暗部分为环境光

三维场景中，只要能模拟出以上三种光照效果，则成功模拟了虚拟世界中的光照。完成产品经理、设计是要求，成功上线则不成问题。

### 3.1、环境光 模拟

从四面八方照射向物体的光，这种光是非发光物体反射的其他光；
因此，`环境光可以选择一个较暗的颜色值进行模拟`。

![在这里插入图片描述](https://img-blog.csdnimg.cn/201908231501362.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

#### shader代码实现

shader 代码实现如下：*选择一个较暗的颜色值模拟环境光*。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150201146.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

### 3.2、散射光(漫反射光) 模拟

光源照射到物体的表面，经过物体表面的漫反射，四面八方反射出去的光。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150215272.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

如上图所示，一个光源照直射到一个球面，半个球面会有相应的反射光，这里要模拟的就是这样的光：
`半球面中心点最亮，一直到球面边缘逐渐变暗`。

#### 公式来计算

我们可以用如下公式来计算散射光：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150231373.png)

+ 光照的`中心点最亮`；
+ 入射角越大，最终的散射光强度就越小；`从中心到边缘慢慢变暗`。

#### shader代码实现

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150243591.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

newNormal 点的法向量、vp 点到光源的向量 均为单位向量，因此，向量点乘即为入射角的cos值；

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150256255.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)


### 3.3、镜面光 模拟

相比散射光，`高光的亮度区域进一步缩小，是光照射到物体中最亮的一块`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150326978.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

+ V点到摄像机的向量
+ L点到光源的向量
+ H V与L的半向量 

#### 模拟公式

我们可以用以下公式来模拟：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150339564.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

#### shader代码实现

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150349861.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly94aWF4bC5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)



## 四、最终效果

+ OpenGLES如何加载.obj与.mtl文件？
+ 勋章的进入和退出动画效果？
+ 勋章进入和退出过程中的实时光照效果该怎么做？

以上三个问题解决完成，我们来瞅瞅最终效果（炫耀一下）：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019082315040248.gif)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150410382.gif)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190823150419858.gif)
## 五、开源

最后，`两个字：代码开源...`

文档地址：
[http://blog.csdn.net/xiaxl/article/details/77048507](http://blog.csdn.net/xiaxl/article/details/77048507)
案例实现代码：
[https://github.com/xiaxveliang/GLES2_Anima_LoadFrom_Obj](https://github.com/xiaxveliang/GLES2_Anima_LoadFrom_Obj)






