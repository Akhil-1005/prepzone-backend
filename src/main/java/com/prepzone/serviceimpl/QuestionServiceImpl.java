package com.prepzone.serviceimpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.prepzone.dto.OptionDTO;
import com.prepzone.dto.QuestionDTO;
import com.prepzone.entity.Chapter;
import com.prepzone.entity.Option;
import com.prepzone.entity.Question;
import com.prepzone.repository.ChapterRepository;
import com.prepzone.repository.QuestionRepository;
import com.prepzone.service.QuestionService;
import com.prepzone.util.ResponseModel;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public ResponseModel<QuestionDTO> createQuestion(QuestionDTO dto, UUID userId) {
        ResponseModel<QuestionDTO> response = new ResponseModel<>();
        try {
            log.info("Begin QuestionServiceImpl -> createQuestion()");
            Chapter chapter = chapterRepository.findById(dto.getChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Chapter not found"));

            Question question = new Question();
            question.setQuestionText(dto.getQuestionText());
            question.setExplanation(dto.getExplanation());
            question.setDifficulty(dto.getDifficulty());
            question.setChapter(chapter);
            question.setCreatedBy(userId);
            question.setQuestionType(dto.getQuestionType() != null ? dto.getQuestionType() : "MCQ");
            question.setCorrectAnswer(dto.getCorrectAnswer());

            if ("MCQ".equalsIgnoreCase(question.getQuestionType()) && dto.getOptions() != null) {
                List<Option> options = dto.getOptions().stream()
                        .map(optDto -> {
                            Option opt = new Option();
                            opt.setOptionText(optDto.getOptionText());
                            opt.setCorrect(optDto.isCorrect());
                            opt.setQuestion(question);
                            opt.setCreatedBy(userId);
                            return opt;
                        }).collect(Collectors.toList());
                question.setOptions(options);
            }
            Question saved = questionRepository.save(question);

            response.setData(mapToDTO(saved));
            response.setMessage("Question saved successfully");
            response.setStatusCode(HttpStatus.CREATED.toString());
            log.info("End QuestionServiceImpl -> createQuestion()");
        } catch (Exception e) {
            log.error("Exception in QuestionServiceImpl -> createQuestion()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<QuestionDTO> updateQuestion(UUID id, QuestionDTO dto, UUID userId) {
        ResponseModel<QuestionDTO> response = new ResponseModel<>();
        try {
            log.info("Begin QuestionServiceImpl -> updateQuestion()");
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            question.setQuestionText(dto.getQuestionText());
            question.setExplanation(dto.getExplanation());
            question.setDifficulty(dto.getDifficulty());
            question.setQuestionType(dto.getQuestionType() != null ? dto.getQuestionType() : "MCQ");
            question.setCorrectAnswer(dto.getCorrectAnswer());

            question.getOptions().clear();
            if ("MCQ".equalsIgnoreCase(question.getQuestionType()) && dto.getOptions() != null) {
                List<Option> updatedOptions = dto.getOptions().stream()
                        .map(optDto -> {
                            Option opt = new Option();
                            opt.setOptionText(optDto.getOptionText());
                            opt.setCorrect(optDto.isCorrect());
                            opt.setQuestion(question);
                            opt.setCreatedBy(userId);
                            return opt;
                        }).collect(Collectors.toList());
                question.getOptions().addAll(updatedOptions);
            }
            Question updated = questionRepository.save(question);

            response.setData(mapToDTO(updated));
            response.setMessage("Question updated successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End QuestionServiceImpl -> updateQuestion()");
        } catch (Exception e) {
            log.error("Exception in QuestionServiceImpl -> updateQuestion()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<QuestionDTO> getQuestionById(UUID id) {
        ResponseModel<QuestionDTO> response = new ResponseModel<>();
        try {
            log.info("Begin QuestionServiceImpl -> getQuestionById()");
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));
            response.setData(mapToDTO(question));
            response.setMessage("Question fetched successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End QuestionServiceImpl -> getQuestionById()");
        } catch (Exception e) {
            log.error("Exception in QuestionServiceImpl -> getQuestionById()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<List<QuestionDTO>> getQuestionsByChapter(UUID chapterId) {
        ResponseModel<List<QuestionDTO>> response = new ResponseModel<>();
        try {
            log.info("Begin QuestionServiceImpl -> getQuestionsByChapter()");
            List<Question> questions = questionRepository.findByChapter_Id(chapterId);
            List<QuestionDTO> questionDTOs = questions.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            response.setData(questionDTOs);
            response.setMessage("Questions fetched successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End QuestionServiceImpl -> getQuestionsByChapter()");
        } catch (Exception e) {
            log.error("Exception in QuestionServiceImpl -> getQuestionsByChapter()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<String> deleteQuestion(UUID id) {
        ResponseModel<String> response = new ResponseModel<>();
        try {
            log.info("Begin QuestionServiceImpl -> deleteQuestion()");
            questionRepository.deleteById(id);
            response.setData("Deleted");
            response.setMessage("Question deleted successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End QuestionServiceImpl -> deleteQuestion()");
        } catch (Exception e) {
            log.error("Exception in QuestionServiceImpl -> deleteQuestion()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    // Mapper
    private QuestionDTO mapToDTO(Question q) {
        return QuestionDTO.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .explanation(q.getExplanation())
                .difficulty(q.getDifficulty())
                .questionType(q.getQuestionType())
                .correctAnswer(q.getCorrectAnswer())
                .chapterId(q.getChapter().getId())
                .options(q.getOptions().stream()
                        .map(opt -> OptionDTO.builder()
                                .id(opt.getId())
                                .optionText(opt.getOptionText())
                                .isCorrect(opt.isCorrect())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
