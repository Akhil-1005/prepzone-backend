package com.prepzone.service;

import java.util.List;
import java.util.UUID;

import com.prepzone.entity.Chapter;
import com.prepzone.util.ResponseModel;

public interface ChapterService {

	ResponseModel<Chapter> createChapter(Chapter chapter, UUID subjectId, UUID userId);

	ResponseModel<List<Chapter>> getChaptersBySubject(UUID subjectId);

	ResponseModel<Chapter> updateChapter(UUID chapterId, Chapter chapter, UUID userId);

	ResponseModel<Void> deleteChapter(UUID chapterId, UUID userId);

}
