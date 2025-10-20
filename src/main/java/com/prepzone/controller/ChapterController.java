package com.prepzone.controller;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prepzone.entity.Chapter;
import com.prepzone.service.ChapterService;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chapters")
@Slf4j
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private HttpStatusCode httpStatusCode;

    @PostMapping("/create")
    public ResponseEntity<ResponseModel<Chapter>> createChapter(@RequestHeader UUID userId, 
                                                                @RequestParam UUID subjectId,
                                                                @RequestBody Chapter chapter) {
        log.info("Begin ChapterController -> createChapter()");
        ResponseModel<Chapter> response = chapterService.createChapter(chapter, subjectId, userId);
        log.info("End ChapterController -> createChapter()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ResponseModel<List<Chapter>>> getChaptersBySubject(@PathVariable UUID subjectId) {
        log.info("Begin ChapterController -> getChaptersBySubject()");
        ResponseModel<List<Chapter>> response = chapterService.getChaptersBySubject(subjectId);
        log.info("End ChapterController -> getChaptersBySubject()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PutMapping("/update/{chapterId}")
    public ResponseEntity<ResponseModel<Chapter>> updateChapter(@RequestHeader UUID userId,
                                                                @PathVariable UUID chapterId,
                                                                @RequestBody Chapter chapter) {
        log.info("Begin ChapterController -> updateChapter()");
        ResponseModel<Chapter> response = chapterService.updateChapter(chapterId, chapter, userId);
        log.info("End ChapterController -> updateChapter()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @DeleteMapping("/delete/{chapterId}")
    public ResponseEntity<ResponseModel<Void>> deleteChapter(@RequestHeader UUID userId,
                                                             @PathVariable UUID chapterId) {
        log.info("Begin ChapterController -> deleteChapter()");
        ResponseModel<Void> response = chapterService.deleteChapter(chapterId, userId);
        log.info("End ChapterController -> deleteChapter()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }
}
