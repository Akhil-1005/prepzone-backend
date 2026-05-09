package com.prepzone.response;

import java.util.UUID;

public interface ChapterBasicProjection {

    UUID getId();          // maps to Chapter.id
    String getChapterName();
}
