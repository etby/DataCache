# DataCache

该项目能够同步两个相同类型对象的某些字段，可以用来在内存中保持针对某个类型某个对象的唯一实例。

一般情况下搭配数据绑定框架，能够轻松的在多个页面间同步展示数据状态。

## 使用方法

### 导入

[![Download](https://api.bintray.com/packages/etby/android/datacache/images/download.svg)](https://bintray.com/etby/android/datacache/_latestVersion)

```groovy
  implementation "org.etby.datacache:datacache:<version>"
  annotationProcessor "org.etby.datacache:datacache-compiler:<version>" // kotlin 使用 kapt
```

### 数据类处理

- 所有需要同步的数据类需要继承接口`Cacheable`，实现两个方法：
  - `getCacheKey` #获取唯一的ID, 使用数据对象的ID即可
  - `getHelper` 使用注解处理器生成的帮助类进行实现即可
- 所有需要同步的数据类也要增加注解
  - 在类上面增加`CacheClass`
  - 在需要同步的字段上增加`CacheField`

具体可参考下面的例子:

```java
@CacheClass public class Foo implements Cacheable {

  @CacheField private String name;
  @CacheField private int age;

  private final CacheHelper helper;

  {
    helper = CacheHelperCreate.newHelper(this);
  }

  @Override public CacheHelper getHelper() {
    return helper;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override public String getCacheKey() {
    return name;
  }
}
```

> 注意: helper 在每个对象中必须一直存在, 不能在实现方法中每次创建

### 标记

在保持唯一的时候, 更新对象数据有可能不全, 这时候需要判断哪些字段是可用, 哪些字段忽略. 在可用字段上打上标记之后, 就只会将这些字段的值更新到内存唯一对象中.

更新方法:

```java
helper.notifyCacheChanged(CF.**); // 传入字段对应的注解处理器生成的ID即可, 和数据绑定类似

helper.notifyCacheChanged(helper.getCF(name)); // 传入字段名称, 获取ID之后更新
```

### 更新

在打好标记之后, 需要将标记对象更新到内存唯一对象中. 之后标记对象会被废弃, 所有使用的都为内存中唯一的对象.

```java
  private static <T extends Cacheable> T update(T target) {
    return DataCache.getInstance().updateCacheable(target);
  }
```

上面是一段简单的工具方法, `target`对象则是打过标记的对象, 此方法返回内存唯一对象.

之后在应用程序中一直使用返回的对象即可.

## 实际使用

在APP内部, 一般情况下获取数据都是通过API, 也就是只要隔离API获取的对象, 则APP内部完全可以保证唯一.

### API隔离

由于API返回数据一般使用像Gson这样的框架解析, 则每次肯定会使用新对象来承载数据, 在API数据返回之后, 需要将其转换更新为内存中的唯一对象, 这样才能够保证整个APP使用的针对某个特定数据的对象只有唯一一个.

#### 标记

对于返回字段由客户端所控制的API来说, 很容易进行标记, 就不在此进行说明了.

对于无法控制的API, 唯一轻松的办法是在解析时候进行标记. 这就需要对于框架进行扩展或者魔改了, 由于比较复杂, 目前对这个暂时没有支持. 不过下面有真实项目中使用Gson的例子, 可以用来参考.

#### Gson Hack

直接对Gson进行魔改, 在解析过程中打上字段标记.

由于所有的普通对象都由下面的类进行解析, 直接对此类中解析方法进行更改就可以进行标记了.

`gson/src/main/java/com/google/gson/internal/bind/ReflectiveTypeAdapterFactory.java`

对其中的`TypeAdapter`的子类的`read`方法进行魔改

```java
    @Override public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }

      T instance = constructor.construct();

      // 获取Helper
      CacheHelper helper = null;
      if (instance instanceof Cacheable) {
        helper = ((Cacheable) instance).getHelper();
      }

      try {
        in.beginObject();
        while (in.hasNext()) {
          String name = in.nextName();
          BoundField field = boundFields.get(name);
          if (field == null || !field.deserialized) {
            in.skipValue();
          } else {
            field.read(in, instance);
            // 当数据设置成功时, 更新Cache
              if (helper != null) {
                  helper.notifyCacheChanged(helper.getCF(name));
              }
          }
        }
      } catch (IllegalStateException e) {
        throw new JsonSyntaxException(e);
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
      in.endObject();
      return instance;
    }
```

比较粗暴的例子, 可以作为参考, 如果有比较优雅的外部扩展的方法, 请联系开发者.

#### 更新

最难的标记完成之后更新则很简单了. 对于有分层的架构来说, 只要在数据层的出口做更新即可, 就可以保证在应用内部数据唯一. 对于普通的架构来说, 则在每一个请求之后进行更新再进行使用即可.

## License

```
   Copyright 2018 Etby

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

