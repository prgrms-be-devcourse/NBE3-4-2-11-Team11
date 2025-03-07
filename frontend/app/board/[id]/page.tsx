// "use client";

// import { useRouter, useParams } from "next/navigation"; // useParams 추가
// import { getPostById, deletePost } from "../../../lib/board";
// import { useEffect, useState } from "react";
// import MarkdownRenderer from "../../../components/MarkdownRenderer";
// import { useAuthStore } from "@/store/authStore";
// import { getAccessToken } from "@/utils/token";
// import ReactMarkdown from "react-markdown";
// import remarkGfm from "remark-gfm";
// import remarkBreaks from "remark-breaks"; // 줄바꿈 처리 추가
// import rehypeHighlight from "rehype-highlight";
// import "highlight.js/styles/github.css";
// import { Post } from "../../../lib/board"; // Post 타입이 정의된 경로로 수정

// // ✅ JWT 토큰에서 로그인한 사용자의 userId를 추출하는 함수
// const getLoggedInUserId = (): number | null => {
//   const token = getAccessToken();
//   if (!token) return null;

//   try {
//     const tokenPayload = JSON.parse(atob(token.split(".")[1])); // 토큰 디코딩
//     return tokenPayload?.userId || null; // JWT에서 userId 가져오기
//   } catch (error) {
//     console.error("토큰 디코딩 실패:", error);
//     return null;
//   }
// };

// const PostDetailPage = () => {
//   const [post, setPost] = useState<Post | null>(null);
//   const router = useRouter();
//   const params = useParams(); // useParams 훅으로 params 가져오기
//   const { isLoggedIn, checkAuthStatus } = useAuthStore(); // 로그인 상태 및 갱신 함수

//   const loggedInUserId = getLoggedInUserId();
//   const postId = params.id as string; // params.id는 문자열로 반환됨

//   // ✅ 로그인 상태 확인 (쿠키 기반 인증 유지)
//   useEffect(() => {
//     if (!isLoggedIn) {
//       checkAuthStatus(); // 로그인 상태 확인 후 갱신
//     }
//   }, [isLoggedIn]);

//   // ✅ 게시글 가져오기
//   useEffect(() => {
//     const fetchPost = async () => {
//       try {
//         const data = await getPostById(Number(postId)); // postId 사용
//         setPost(data);
//       } catch (error) {
//         console.error("게시글 불러오기 실패:", error);
//       }
//     };
//     fetchPost();
//   }, [postId]);

//     // ✅ 게시글 삭제 (수정 방식과 동일한 방식 적용)
//     const handleDelete = async () => {
//       if (!isLoggedIn) {
//         alert("로그인 후 게시글을 삭제할 수 있습니다.");
//         router.push("/login");
//         return;
//       }
  
//       if (!post) {
//         alert("게시글 정보를 불러오지 못했습니다.");
//         return;
//       }

//     // ✅ 작성자 검증 (현재 로그인한 userId와 게시글 userId 비교)
//     if (!post || !loggedInUserId || loggedInUserId !== post.userId) {
//       alert("작성자만 게시글을 삭제할 수 있습니다.");
//       return;
//     }

//     if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
//       try {
//         await deletePost(Number(postId)); // postId 사용
//         alert("게시글이 성공적으로 삭제되었습니다.");
//         router.push("/board"); // 삭제 후 목록 페이지로 이동
//       } catch (error) {
//         console.error("게시글 삭제 실패:", error);
//         alert("게시글 삭제 중 오류가 발생했습니다.");
//       }
//     }
//   };

//   if (!post) {
//     return (
//       <div className="min-h-screen flex items-center justify-center bg-gray-100">
//         <p className="text-lg text-gray-600">게시글을 불러오는 중...</p>
//       </div>
//     );
//   }

//   return (
//     <div className="min-h-screen bg-gray-100 p-8 flex justify-center">
//       <main className="w-full max-w-4xl bg-white p-6 rounded shadow-md">
//         <div className="flex items-center mb-4">
//           <div className="bg-white text-black p-4 rounded border w-3/4">
//             <h1 className="text-3xl font-bold">{post.title}</h1>
//           </div>
//           <div className="text-right w-1/4 ml-4">
//             <p className="text-gray-600 text-sm mt-2">
//               {new Date(post.createdAt).toLocaleDateString()}
//             </p>
//           </div>
//         </div>

//         {/* ✅ 마크다운 적용된 본문 렌더링 */}
//         <div className="bg-white text-black p-6 mb-4 rounded h-[60vh] overflow-y-auto border">
//           <MarkdownRenderer content={post.content} /> {/* 마크다운 렌더링 적용 */}
//         </div>

//         <div className="flex justify-end space-x-4">
//           <button
//             onClick={() => router.push(`/board/${postId}/edit`)}
//             className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
//           >
//             수정
//           </button>
//           <button
//             onClick={handleDelete}
//             className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
//           >
//             삭제
//           </button>
//         </div>
//       </main>
//     </div>
//   );
// };

// export default PostDetailPage;

"use client";

import { useRouter, useParams } from "next/navigation";
import { getPostById, deletePost } from "../../../lib/board";
import { useEffect, useState } from "react";
import MarkdownRenderer from "../../../components/MarkdownRenderer";
import { useAuthStore } from "@/store/authStore";
import { getAccessToken } from "@/utils/token";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import remarkBreaks from "remark-breaks";
import rehypeHighlight from "rehype-highlight";
import "highlight.js/styles/github.css";
import { Post } from "../../../lib/board"; // Post 타입이 정의된 경로로 수정


const PostDetailPage = () => {
  const [post, setPost] = useState<Post | null>(null);
  const router = useRouter();
  const params = useParams();
  const { isLoggedIn, checkAuthStatus } = useAuthStore();

  const postId = params.id as string;

  // ✅ 로그인 상태 확인
  useEffect(() => {
    if (!isLoggedIn) {
      checkAuthStatus();
    }
  }, [isLoggedIn]);

  // ✅ 게시글 가져오기
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const data = await getPostById(Number(postId));
        setPost(data);
      } catch (error) {
        console.error("게시글 불러오기 실패:", error);
      }
    };
    fetchPost();
  }, [postId]);

  
  // ✅ 게시글 삭제 (board.ts의 deletePost() 사용)
  const handleDelete = async () => {
    if (!isLoggedIn) {
      alert("로그인 후 게시글을 삭제할 수 있습니다.");
      router.push("/login");
      return;
    }

   

    // // ✅ 작성자 검증
    // if (!post.userId || loggedInUserId !== post.userId) {
    //   alert("작성자만 게시글을 삭제할 수 있습니다.");
    //   return;
    // }

    if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
      try {
        await deletePost(Number(postId)); // ✅ board.ts의 deletePost() 호출
        alert("게시글이 성공적으로 삭제되었습니다.");
        router.push("/board");
      } catch (error) {
        console.error("게시글 삭제 실패:", error);
        alert("게시글 삭제 중 오류가 발생했습니다.");
      }
    }
  };

  if (!post) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-lg text-gray-600">게시글을 불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 p-8 flex justify-center">
      <main className="w-full max-w-4xl bg-white p-6 rounded shadow-md">
        <div className="flex items-center mb-4">
          <div className="bg-white text-black p-4 rounded border w-3/4">
            <h1 className="text-3xl font-bold">{post.title}</h1>
          </div>
          <div className="text-right w-1/4 ml-4">
            <p className="text-gray-600 text-sm mt-2">
              {new Date(post.createdAt).toLocaleDateString()}
            </p>
          </div>
        </div>

        {/* ✅ 마크다운 적용된 본문 렌더링 */}
        <div className="bg-white text-black p-6 mb-4 rounded h-[60vh] overflow-y-auto border">
          <MarkdownRenderer content={post.content} />
        </div>

        <div className="flex justify-end space-x-4">
          <button
            onClick={() => router.push(`/board/${postId}/edit`)}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            수정
          </button>
          <button
            onClick={handleDelete}
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
          >
            삭제
          </button>
        </div>
      </main>
    </div>
  );
};

export default PostDetailPage;
