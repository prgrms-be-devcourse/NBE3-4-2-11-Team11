package com.pofo.backend.domain.inquiry.dto.response;

import com.pofo.backend.domain.comment.dto.response.CommentDetailResponse;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.reply.dto.response.ReplyDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class InquiryDetailResponse {

    private Long userId;
    private Long id;
    private String subject;
    private String content;
    private int response;
    private LocalDateTime createdAt;
    private List<Object> repliesAndComments;

    public static InquiryDetailResponse from(Inquiry inquiry, List<ReplyDetailResponse> replies, List<CommentDetailResponse> comments) {
        // null 처리: replies와 comments가 null일 경우 빈 리스트로 초기화
        replies = (replies != null) ? replies : new ArrayList<>();
        comments = (comments != null) ? comments : new ArrayList<>();

        // 댓글과 답변을 하나의 리스트로 합치고 createdAt 기준으로 정렬
        List<Object> sortedList = Stream.concat(replies.stream(), comments.stream())
                .sorted(Comparator.comparing(item -> {
                    if (item instanceof ReplyDetailResponse) return ((ReplyDetailResponse) item).getCreatedAt();
                    else return ((CommentDetailResponse) item).getCreatedAt();
                }))
                .collect(Collectors.toList());

        return new InquiryDetailResponse(
                inquiry.getUser().getId(),
                inquiry.getId(),
                inquiry.getSubject(),
                inquiry.getContent(),
                inquiry.getResponse(),
                inquiry.getCreatedAt(),
                sortedList // 하나의 리스트로 반환
        );
    }
}
