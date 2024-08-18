# Ytdl-Java

A simple Java library for fetching YouTube video information with support for both synchronous and asynchronous operations.

## Features

- **Easy to Use:** Fetch video information with a single line of code.
- **Asynchronous Support:** Use callbacks for non-blocking operations.
- **Efficient Caching:** Reduces redundant network requests.
- **Powered by OkHttp:** For fast and reliable HTTP requests.

## Installation

Include Ytdl-Java in your project using Maven or Gradle.

### Maven

```xml
<dependency>
    <groupId>io.github.luoshenshi</groupId>
    <artifactId>ytdl-java</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle (Kotlin)

```groovy
implementation("io.github.luoshenshi:ytdl-java:1.0")
```

## Usage

### Synchronous Example

Fetch video information synchronously:

```java
VideoInfo info = YTDL.getInfo("kJQP7kiw5Fk");
System.out.println(info.getAuthorInfo());
```

### Asynchronous Example

Fetch video information asynchronously with a callback:

```java
YTDL.getInfo("eYfjh0U6uQU", new YtdlResponse<>() {
  @Override
  public void onResponse(VideoInfo videoInfo) {
    System.out.println(videoInfo.getFormats());
  }

  @Override
  public void onFailure(Exception e) {
    throw new IllegalArgumentException(e.getLocalizedMessage());
  }
});

System.out.println("Request sent!");
```

## API Documentation

Ytdl-Java provides an intuitive API to interact with YouTube data. Here are some of the key classes and methods:

- **`YTDL`**
    - `getInfo(String videoId)`: Retrieves video information synchronously.
    - `getInfo(String videoId, YtdlResponse<VideoInfo> response)`: Retrieves video information asynchronously.

- **`VideoInfo`**
    - `getTitle()`: Returns the title of the video.
    - `getAuthorInfo()`: Returns the author's information.
    - `getLikes()`: Returns the number of likes.
    - `getViews()`: Returns the number of views.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.