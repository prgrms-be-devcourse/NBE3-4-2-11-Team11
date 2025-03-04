"use client";

import React, { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import axios from "axios";
import EditPopup from "./editPopup";
import styles from "./inquiryDetail.module.css";

type ReplyDetailResponse = {
  id: number;
  createdAt: string;
  content: string;
  type?: "comment" | "reply";
};

type CommentDetailResponse = ReplyDetailResponse;

type InquiryDetailResponse = {
  userId: number;
  id: number;
  subject: string;
  content: string;
  createdAt: string; // LocalDateTime을 문자열로 변환
  repliesAndComments: Array<ReplyDetailResponse | CommentDetailResponse>; // 댓글과 답변 리스트
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

const InquiryDetailPage = () => {
  const { id } = useParams();
  const router = useRouter();
  const [inquiry, setInquiry] = useState<InquiryDetailResponse | null>(null);
  const [repliesAndComments, setRepliesAndComments] = useState<
    Array<ReplyDetailResponse | CommentDetailResponse>
  >([]);
  const [newContent, setNewContent] = useState(""); // 새 댓글/답변 입력값
  const [editingId, setEditingId] = useState<number | null>(null); // 현재 수정 중인 댓글/답변 ID
  const [editingContent, setEditingContent] = useState<string>(""); // 수정 중인 content
  const [error, setError] = useState<string | null>(null); // 에러 상태 추가
  const [showEditPopup, setShowEditPopup] = useState<boolean>(false);
  const [token, setToken] = useState<string | null>(null); // token 상태 정의
  const [role, setRole] = useState<string | null>(null); // 역할 상태 추가

   const checkIfAdminFromToken = (role) => {
    return role === "ADMIN";  // 역할이 ADMIN이면 true 반환
    };

useEffect(() => {
  // 로그인 상태 체크 API 호출
  axios.get('/api/v1/auth/status', { withCredentials: true })  // 쿠키를 자동으로 포함하도록 설정
    .then((response) => {
      console.log(response.data);

      if (response.data.isLoggedIn) {
        setToken("authenticated"); // 로그인된 상태를 의미하는 값
        setRole(response.data.role); // 역할 정보 저장
      } else {
        setToken(null);
        setRole(null);
      }
    })
    .catch((error) => {
      console.error("로그인 상태 확인 오류:", error);
      setToken(null);
      setRole(null);
    });
}, []);

  const handleCreate = async () => {
    if (!newContent.trim()) {
      alert("내용을 입력해주세요!");
      return;
    }

    if (!token) {
      alert("로그인이 필요합니다."); // 토큰 값이 없는 경우 처리
      return;
    }

    // 토큰 권한 확인 및 엔드포인트 결정
    const isAdmin = checkIfAdminFromToken(token); // 관리자 여부 확인
    const endpoint = isAdmin
      ? `/api/v1/admin/inquiries/${id}/reply`
      : `/api/v1/user/inquiries/${id}/comment`;

    try {
      const payload = { inquiryId: id, content: newContent };

      const response = await axios.post(endpoint, payload, { withCredentials: true });

      // 리스트 초기화 및 성공 메시지
      setNewContent("");
      setError(null);
      alert(
        isAdmin
          ? "답변이 성공적으로 등록되었습니다!"
          : "댓글이 성공적으로 등록되었습니다!"
      );
      window.location.reload();
    } catch (err: any) {
      // 주어진 에러 객체를 분석
      if (err.response) {
        // 서버가 반환한 에러 응답 (status 코드 & data)
        console.error(
          "Server response error:",
          err.response.status,
          err.response.data
        );
        setError(
          err.response.data?.message || "서버 오류가 발생했습니다."
        );
      } else if (err.request) {
        // 서버에 요청이 도달하지 못한 경우
        console.error("No response received:", err.request);
        setError(
          "서버로부터 응답이 없습니다. 잠시 후 다시 시도해주세요."
        );
      } else {
        // 기타 클라이언트 측 에러
        console.error("Axios request error:", err.message);
        setError("요청 중 문제가 발생했습니다.");
      }
    }
  };

  const handleEdit = (replyOrComment: ReplyDetailResponse) => {
    setEditingId(replyOrComment.id);
    setEditingContent(replyOrComment.content); // 기존 내용을 편집 폼에 반영
    setShowEditPopup(true);
  };

  // 수정 내용 제출
    const handleEditSubmit = async (newContent: string) => {
    if (!editingId) return;

    // isAdmin 선언
    const isAdmin = checkIfAdminFromToken(token); // 관리자 여부 확인

    const endpoint = isAdmin
      ? `/api/v1/admin/inquiries/${id}/reply/${editingId}`
      : `/api/v1/user/inquiries/${id}/comment/${editingId}`;

    try {
      await axios.patch(endpoint, { content: newContent, withCredentials: true }
      );
      alert("성공적으로 수정되었습니다!");
      setEditingId(null);
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("수정에 실패했습니다.");
    }
  };

  // 삭제 버튼 로직
const handleDelete = async (id: number, type: 'comment' | 'reply',  editingId: number ) => {
    const confirmDelete = confirm(
      type === 'comment'
        ? '댓글을 삭제하시겠습니까?'
        : '답변을 삭제하시겠습니까?'
    );
  if (!id) {
    alert("유효하지 않은 요청입니다.");
    return;
  }


    // 댓글/답변 타입에 따라 다른 엔드포인트 설정
    const endpoint =
      type === 'comment'
        ? `/api/v1/user/inquiries/${id}/comment/${editingId}` // 댓글 삭제
        : `/api/v1/admin/inquiries/${id}/reply/${editingId}`; // 답변 삭제

    try {
      await axios.delete(endpoint, { withCredentials: true });
      alert('성공적으로 삭제되었습니다!');
      window.location.reload();
    } catch (error) {
      console.error('삭제 중 오류 발생:', error);
      alert('삭제에 실패했습니다.');
    }
  };

  useEffect(() => {
    const fetchInquiry = async () => {
      try {
        const response = await axios.get<RsData<InquiryDetailResponse>>(
          `/api/v1/common/inquiries/${id}`);
        setInquiry(response.data.data); // Inquiry 정보와 repliesAndComments를 설정
      } catch (error) {
        console.error("Error fetching inquiry detail:", error);
        alert("문의글 정보를 불러오는 중 오류가 발생했습니다.");
      }
    };

    if (id && token) {
          fetchInquiry();
        }
      }, [id, token]);

    if (!inquiry) return <div>Loading...</div>;

   return (
       <div className="p-4 relative">
         <div className="absolute top-12 right-4 flex space-x-2">
           <button
             onClick={() => {
               if (confirm("해당 문의글을 삭제하시겠습니까?")) {
                 axios.delete(`/api/v1/user/inquiries/${id}`,  { withCredentials: true });
                 alert("문의가 성공적으로 삭제되었습니다!");
                 router.push("/inquiry");
               }
             }}
             className="text-sm text-gray-500 hover:underline"
           >
             삭제
           </button>
           <button
             onClick={() => router.push(`/inquiry/edit/${id}`)}
             className="text-sm text-gray-500 hover:underline"
           >
             수정
           </button>
         </div>
         <div className={styles.inquiryDetailContainer}>
         {/* 문의 제목 및 상세 정보 출력 */}
          <div className={styles.inquiryDetailSubjectRow}>
           <h1 className={styles.inquiryDetailHeader}>{inquiry.subject}</h1>
           <p className={styles.inquiryDetailDate}>{new Date(inquiry.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {/* 구분선 */}
          <hr className={styles.inquiryDetailDivider}/>
          {/* 본문 출력 */}
          <div className={styles.inquiryDetailContent}>{inquiry.content}</div>
      </div>

{/* 댓글과 답변 */}
<div className={styles.replyContainer}>
  <h2 className={styles.replyAndCommentHeader}>댓글 및 답변</h2>
  {inquiry.repliesAndComments.map((replyOrComment) => {
    if (replyOrComment.type === 'comment') {
      return (
        <div key={replyOrComment.id} className={styles.replyContainer}>
          <div className={styles.replyHeader}>

            {/* 수정/삭제 버튼 추가 */}
            <div className="flex justify-end space-x-2">
              <button
                className="text-blue-500 text-sm hover:underline"
                onClick={() => handleEdit(replyOrComment)}
              >
                수정
              </button>
              <button
                className="text-red-500 text-sm hover:underline"
                onClick={() => handleDelete(inquiry.id, 'comment', replyOrComment.id)}
              >
                삭제
              </button>
            </div>
          </div>
          {/* 댓글 내용 */}
          {editingId === replyOrComment.id ? (
            <>
              <textarea
                value={editingContent}
                onChange={(e) => setEditingContent(e.target.value)}
                className="w-full px-4 py-2 border rounded"
              />
              <div className="flex justify-end space-x-2 mt-2">
                <button
                  onClick={() => handleEditSubmit(editingContent)}
                  className="bg-green-500 text-white px-4 py-2 rounded"
                >
                  확인
                </button>
                <button
                  onClick={() => setEditingId(null)}
                  className="bg-gray-500 text-white px-4 py-2 rounded"
                >
                  취소
                </button>
              </div>
            </>
          ) : (
            <div className={styles.replyContent}>
              <strong style={{ fontSize: '20px' }}>댓글</strong>
              <br />
              {replyOrComment.content}
            </div>
          )}
          <span className={styles.replyDate}>{replyOrComment.createdAt}</span>
        </div>
      );
    } else if (replyOrComment.type === 'reply') {
      return (
        <div key={replyOrComment.id} className={styles.replyContainer}>
          <div className={styles.replyHeader}>
            {/* 수정/삭제 버튼 추가 */}
            <div className="flex justify-end space-x-2">
              <button
                className="text-blue-500 text-sm hover:underline"
                onClick={() => handleEdit(replyOrComment)}
              >
                수정
              </button>
              <button
                className="text-red-500 text-sm hover:underline"
                onClick={() => handleDelete(inquiry.id, 'reply', replyOrComment.id)}
              >
                삭제
              </button>
            </div>
          </div>
          {/* 답변 내용 */}
          {editingId === replyOrComment.id ? (
            <>
              <textarea
                value={editingContent}
                onChange={(e) => setEditingContent(e.target.value)}
                className="w-full px-4 py-2 border rounded"
              />
              <div className="flex justify-end space-x-2 mt-2">
                <button
                  onClick={() => handleEditSubmit(editingContent)}
                  className="bg-green-500 text-white px-4 py-2 rounded"
                >
                  확인
                </button>
                <button
                  onClick={() => setEditingId(null)}
                  className="bg-gray-500 text-white px-4 py-2 rounded"
                >
                  취소
                </button>
              </div>
            </>
          ) : (
            <div className={styles.replyContent}>
              <strong style={{ fontSize: '20px' }}>답변</strong>
              <br />
              {replyOrComment.content}
            </div>
          )}
          <span className={styles.replyDate}>{replyOrComment.createdAt}</span>
        </div>
      );
    }
  })}
</div>
{/* 댓글/답변 작성 UI */}
<div className="mt-4 text-right">
  <textarea
    value={newContent}
    onChange={(e) => setNewContent(e.target.value)}
    placeholder="내용을 입력해주세요."
    rows={4}
    className="w-full px-4 py-2 border rounded"
  ></textarea>
  <button
    onClick={handleCreate}
    className="mt-2 bg-blue-500 text-white px-4 py-2 rounded"
  >
    작성하기
  </button>
  {error && <p className="mt-2 text-red-500">{error}</p>}
</div>
</div>
   );
};

export default InquiryDetailPage;