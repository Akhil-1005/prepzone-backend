package com.prepzone.serviceimpl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.prepzone.entity.Chapter;
import com.prepzone.entity.Subject;
import com.prepzone.repository.ChapterRepository;
import com.prepzone.repository.SubjectRepository;
import com.prepzone.service.ChapterService;
import com.prepzone.util.ResponseModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ChapterServiceImpl implements ChapterService {
	
	private final SubjectRepository subjectRepository;
	private final ChapterRepository chapterRepository;


	public ResponseModel<Chapter> createChapter(Chapter chapter, UUID subjectId, UUID userId) {
        ResponseModel<Chapter> response = new ResponseModel<>();
        try {
            log.info("Begin ChapterService -> createChapter()");
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            chapter.setSubject(subject);
            chapter.setCreatedBy(userId);
            Chapter savedChapter = chapterRepository.save(chapter);
            response.setData(savedChapter);
            response.setStatusCode(HttpStatus.CREATED.toString());
            response.setMessage("Chapter saved successfully");
            log.info("End ChapterService -> createChapter()");
        } catch (Exception e) {
            log.error("Exception in ChapterService -> createChapter()", e);
            response.setData(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(e.getMessage());
        }
        return response;
    }

	@Override
	public ResponseModel<List<Chapter>> getChaptersBySubject(UUID subjectId) {
		 ResponseModel<List<Chapter>> response = new ResponseModel<>();
	        try {
	            log.info("Begin ChapterService -> getChaptersBySubject()");
	            List<Chapter> chapters = chapterRepository.findBySubjectId(subjectId);
	            response.setData(chapters);
	            response.setStatusCode(HttpStatus.OK.toString());
	            response.setMessage("Chapters fetched successfully");
	            log.info("End ChapterService -> getChaptersBySubject()");
	        } catch (Exception e) {
	            log.error("Exception in ChapterService -> getChaptersBySubject()", e);
	            response.setData(null);
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            response.setMessage(e.getMessage());
	        }
	        return response;
	}

	@Override
	public ResponseModel<Chapter> updateChapter(UUID chapterId, Chapter chapterDetails, UUID userId) {
	     ResponseModel<Chapter> response = new ResponseModel<>();
	        try {
	            log.info("Begin ChapterService -> updateChapter()");
	            Chapter chapter = chapterRepository.findById(chapterId)
	                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
	            chapter.setChapterName(chapterDetails.getChapterName());
	            Chapter updatedChapter = chapterRepository.save(chapter);
	            response.setData(updatedChapter);
	            response.setStatusCode(HttpStatus.OK.toString());
	            response.setMessage("Chapter updated successfully");
	            log.info("End ChapterService -> updateChapter()");
	        } catch (Exception e) {
	            log.error("Exception in ChapterService -> updateChapter()", e);
	            response.setData(null);
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            response.setMessage(e.getMessage());
	        }
	        return response;
	}

	@Override
	public ResponseModel<Void> deleteChapter(UUID chapterId, UUID userId) {
		ResponseModel<Void> response = new ResponseModel<>();
        try {
            log.info("Begin ChapterService -> deleteChapter()");
            chapterRepository.deleteById(chapterId);
            response.setData(null);
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Chapter deleted successfully");
            log.info("End ChapterService -> deleteChapter()");
        } catch (Exception e) {
            log.error("Exception in ChapterService -> deleteChapter()", e);
            response.setData(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(e.getMessage());
        }
        return response;
    }
	

}
