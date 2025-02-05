## Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)

[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)


### Lesson 01


#### Step 1: maven pom.xml settings

 <properties>
        <main.class>org.example.runtime.MainKt</main.class>   <!-- Lesson01 :: add to pom.xml -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <kotlin.code.style>official</kotlin.code.style>
        <kotlin.compiler.jvmTarget>1.8</kotlin.compiler.jvmTarget>

        <!-- DEPENDENCIES VERSIONS --> <!-- Add this to pom.xml -->
        <slf4j.version>2.0.12</slf4j.version>
        <pi4j.version>2.6.1</pi4j.version>

        <!-- BUILD PLUGIN VERSIONS --> <!-- Add this to pom.xml -->
        <maven-compiler-plugin.version>3.8.1</maven-compiler-plugin.version>
        <maven-jar-plugin.version>3.1.0</maven-jar-plugin.version>
        <maven-shade-plugin.version>3.2.4</maven-shade-plugin.version>
    </properties>

    <repositories>


#### Step 2: remote compiling / debugging setup




![screenshot](readme/readme01.png)


![screenshot](readme/readme02.png)
