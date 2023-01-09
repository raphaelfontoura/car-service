Para adicionar a dependência em aplicações que usam maven, deve-se inserir a dependência kotlinx-coroutines-core:

```xml
<!-- https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core -->
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.6.4</version>
    <type>pom</type>
</dependency>
```

Além de inserir o bom:

```xml
<dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.jetbrains.kotlinx</groupId>
                <artifactId>kotlinx-coroutines-bom</artifactId>
                <version>1.6.4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
</dependencyManagement>
```

No gradle é um pouco diferente. Sendo necessário adicionar:

```properties
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
```

Além de adicionar o seguinte plugin:

```properties
plugins {
// For build.gradle.kts (Kotlin DSL)
kotlin("jvm") version "1.6.21"

    // For build.gradle (Groovy DSL)
    id "org.jetbrains.kotlin.jvm" version "1.6.21"
}
```
