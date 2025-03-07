"use client";

import { useState, useEffect } from "react";
import { getPostById, updatePost } from "../../../../lib/board";
import { useRouter, useParams } from "next/navigation";
import { useAuthStore } from "@/store/authStore"; //로그인 상태 가져오기
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import remarkBreaks from "remark-breaks"; //줄바꿈 지원 추가
import rehypeHighlight from "rehype-highlight"; //코드 하이라이트 추가
import "highlight.js/styles/github.css"; //코드 블록 스타일 적용

const EditPostPage = () => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const router = useRouter();
  const params = useParams();
  const { isLoggedIn, checkAuthStatus } = useAuthStore(); // 로그인 상태 확인

  const postId = params.id as string;

  // 로그인 상태 확인 (WritePostPage 방식 적용)
  useEffect(() => {
    checkAuthStatus();
  }, []);

  // 게시글 데이터 가져오기
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const post = await getPostById(Number(postId));
        setTitle(post.title);
        setContent(post.content);
      } catch (error) {
        console.error("게시글 조회 실패:", error);
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
        router.push("/board");
      }
    };

    if (postId) fetchPost();
  }, [postId]);

  // 게시글 수정 요청
  const handleSubmit = async () => {
    if (!isLoggedIn) {
      alert("로그인 후 게시글을 수정할 수 있습니다.");
      router.push("/login");
      return;
    }

    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    try {
      await updatePost(Number(postId), { title, content }); 
      router.push("/board");
    } catch (error) {
      console.error("게시글 수정 실패:", error);
      alert("게시글 수정 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
      {/* 입력 폼 */}
      <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
        <h1 className="text-2xl font-bold mb-4">게시글 수정</h1>

        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full p-2 mb-4 border rounded"
          placeholder="제목을 입력하세요"
        />

        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="w-full p-2 mb-4 border rounded flex-1"
          placeholder="내용을 입력하세요"
        />

        <button
          onClick={handleSubmit}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
        >
          수정하기
        </button>
      </div>

      {/* 미리보기 (WritePostPage와 동일하게 적용) */}
      <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
        <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
        <div className="prose max-w-none p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
          {content ? (
            <ReactMarkdown
              remarkPlugins={[remarkGfm, remarkBreaks]} // GFM & 줄바꿈 적용
              rehypePlugins={[rehypeHighlight]} // 코드 하이라이트 적용
            >
              {content}
            </ReactMarkdown>
          ) : (
            <p className="text-gray-400">
              내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

export default EditPostPage;
