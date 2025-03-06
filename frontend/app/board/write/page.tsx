// // "use client";

// // import { useState } from 'react';
// // import { createPost } from '../../../lib/board';
// // import { useRouter } from 'next/navigation';
// // import { useAuthStore } from '@/store/authStore';  // 로그인 상태 가져오기
// // // import { getAccessToken } from '@/utils/token';    // 토큰 가져오기
// // // import { decodeJWT } from '@/utils/decodeJWT';     // JWT 디코딩
// // import ReactMarkdown from 'react-markdown';
// // import remarkGfm from 'remark-gfm';
// // import remarkBreaks from 'remark-breaks'; // 줄바꿈 처리 추가

// // import rehypeHighlight from "rehype-highlight";
// // import "highlight.js/styles/github.css";


// // const WritePostPage = () => {
// //   const [title, setTitle] = useState("");
// //   const [content, setContent] = useState("");
// //   const router = useRouter();
// //   const { isLoggedIn } = useAuthStore(); // ✅ 로그인 여부 확인

// //   const handleSubmit = async () => {
// //     if (!isLoggedIn) {
// //       alert("로그인 후 게시글을 작성할 수 있습니다.");
// //       router.push("/login");
// //       return;
// //     }

// //   try {
// //    // ✅ 게시글 생성 API 호출 (email 제거)
// //       const response = await fetch("/api/v1/user/boards", {
// //         method: "POST",
// //         headers: { "Content-Type": "application/json" },
// //         body: JSON.stringify({ title, content }), // ✅ email 제거
// //         credentials: "include", // ✅ 로그인된 사용자 정보 자동 포함 (쿠키 기반 인증)
// //       });

// //       if (!response.ok) {
// //         throw new Error("게시글 작성 실패");
// //       }

// //       // 작성 완료 후 게시판으로 이동
// //       router.push("/board");
// //     } catch (error: any) {
// //       console.error("게시글 작성 실패:", error);
// //       alert(`게시글 작성 중 오류가 발생했습니다: ${error.message}`);
// //     }
// //   };

// //   return (
// //     <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
// //     <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
// //       <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>

// //       <input
// //         type="text"
// //         placeholder="제목을 입력하세요"
// //         value={title}
// //         onChange={(e) => setTitle(e.target.value)}
// //         className="w-full p-2 mb-4 border rounded"
// //       />

// //       <textarea
// //         placeholder="내용을 마크다운 형식으로 작성하세요"
// //         value={content}
// //         onChange={(e) => setContent(e.target.value)}
// //         className="w-full p-2 mb-4 border rounded flex-1"
// //       />

// //       <button
// //         onClick={handleSubmit}
// //         className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
// //       >
// //         작성하기
// //       </button>
// //     </div>
// //           {/* 미리보기 */}
// //       <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
// //         <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
// //         <div className="prose max-w-none p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
// //           {content ? (
// //             <ReactMarkdown
// //               remarkPlugins={[remarkGfm, remarkBreaks]} 
// //               rehypePlugins={[rehypeHighlight]}
// //             >
// //               {content}
// //             </ReactMarkdown>
// //           ) : (
// //             <p className="text-gray-400">
// //               내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.
// //             </p>
// //           )}
// //          </div>
// //       </div>
// //     </div>
// //   );
// // };

// // export default WritePostPage;
// "use client";
// import { useEffect } from "react";
// import { useState } from "react";
// import { useRouter } from "next/navigation";
// import { useAuthStore } from "@/store/authStore";
// import ReactMarkdown from "react-markdown";
// import remarkGfm from "remark-gfm";
// import remarkBreaks from "remark-breaks"; 
// import rehypeHighlight from "rehype-highlight";
// import "highlight.js/styles/github.css";

// const WritePostPage = () => {
//   const [title, setTitle] = useState("");
//   const [content, setContent] = useState("");
//   const router = useRouter();
//   const { isLoggedIn } = useAuthStore(); // ✅ 로그인 여부 확인



//     // ✅ 페이지 로드 시 쿠키에서 사용자 정보 가져오기
//     useEffect(() => {
//       const token = getAccessToken();
//       if (token) {
//         const decodedUser = decodeJWT(token);
//         if (decodedUser) {
//           setUserInfo(decodedUser);
//           console.log("✅ 로그인된 사용자 정보:", decodedUser);
//         }
//       }
//     }, []);

//   const handleSubmit = async () => {
//     if (!isLoggedIn) {
//       alert("로그인 후 게시글을 작성할 수 있습니다.");
//       router.push("/login");
//       return;
//     }
  
//     if (!title.trim() || !content.trim()) {
//       alert("제목과 내용을 입력해주세요.");
//       return;
//     }
  
//     try {
//       console.log("게시글 작성 요청 중...");

//       const token = localStorage.getItem("accessToken"); // ✅ JWT 토큰 가져오기
//       if (!token) {
//         alert("인증 정보가 없습니다. 다시 로그인해 주세요.");
//         router.push("/login");
//         return;
//       }
      
//       const requestBody = { title, content };
//       console.log("요청 데이터:", requestBody);  // 요청 데이터 확인
  
//       const response = await fetch("/api/v1/user/boards", {
//         method: "POST",
//         headers: { "Content-Type": "application/json",
//                    "Authorization": `Bearer ${token}`           
//         },
//         body: JSON.stringify(requestBody),  // JSON 데이터 변환
//         credentials: "include", // 로그인된 사용자 정보 자동 포함 (쿠키 기반 인증)
//       });
  
//       console.log("✅ 서버 응답 상태 코드:", response.status); // 응답 상태 코드 확인
  
//       const responseData = await response.json().catch(() => null);
//       console.log("✅ 서버 응답 데이터:", responseData);  // 백엔드 응답 확인
  
//       if (!response.ok) {
//         throw new Error(`게시글 작성 실패 (상태 코드: ${response.status})`);
//       }
  
//       setTitle("");
//       setContent("");
  
//       router.push("/board");  // ✅ 게시글 작성 완료 후 목록 페이지로 이동
//     } catch (error: any) {
//       console.error("❌ 게시글 작성 실패:", error);
//       alert(`게시글 작성 중 오류가 발생했습니다: ${error.message}`);
//     }
//   };

//   return (
//     <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
//       <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
//         <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>

//         <input
//           type="text"
//           placeholder="제목을 입력하세요"
//           value={title}
//           onChange={(e) => setTitle(e.target.value)}
//           className="w-full p-2 mb-4 border rounded"
//         />

//         <textarea
//           placeholder="내용을 마크다운 형식으로 작성하세요"
//           value={content}
//           onChange={(e) => setContent(e.target.value)}
//           className="w-full p-2 mb-4 border rounded flex-1"
//         />

//         <button
//           onClick={handleSubmit}
//           className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
//         >
//           작성하기
//         </button>
//       </div>

//       {/* ✅ 미리보기 영역 (ReactMarkdown 사용) */}
//       <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
//         <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
//         <div className="prose max-w-none p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
//           {content ? (
//             <ReactMarkdown
//               remarkPlugins={[remarkGfm, remarkBreaks]} 
//               rehypePlugins={[rehypeHighlight]}
//             >
//               {content}
//             </ReactMarkdown>
//           ) : (
//             <p className="text-gray-400">
//               내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.
//             </p>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default WritePostPage;
// -------------------------------------------------------------
// "use client";
// import { useEffect, useState } from "react";
// import { useRouter } from "next/navigation";
// import {createPost} from "@/lib/board"
// import { useAuthStore } from "@/store/authStore";
// import ReactMarkdown from "react-markdown";
// import remarkGfm from "remark-gfm";
// import remarkBreaks from "remark-breaks";
// import rehypeHighlight from "rehype-highlight";
// import "highlight.js/styles/github.css";

// import axios from 'axios';

// // ✅ 게시글 타입 정의 (API 응답 구조와 일치)
// interface Post {
//   id: number;
//   title: string;
//   content: string;
//   createdAt: string;
//   updatedAt?: string;
// }

// // ✅ API 응답 타입 정의 (제너릭 타입 사용)
// interface RsData<T> {
//   status: string;
//   data: T;
//   message?: string;
// }

// // ✅ 로그인 상태 응답 타입
// interface AuthStatusResponse {
//   isLoggedIn: boolean;
//   role?: string;
// }

// const WritePostPage = () => {
//   const [title, setTitle] = useState("");
//   const [content, setContent] = useState("");
//   const [isLoggedIn, setIsLoggedIn] = useState(false);
//   const [role, setRole] = useState<string | null>(null);
//   const [loading, setLoading] = useState(false);
//   const router = useRouter();

//   // ✅ useEffect로 로그인 상태 확인
//   useEffect(() => {
//     axios
//       .get<AuthStatusResponse>("/api/v1/auth/status", { withCredentials: true })
//       .then((response) => {
//         console.log(response.data);
//         const { isLoggedIn, role } = response.data;
//         setIsLoggedIn(isLoggedIn);
//         setRole(isLoggedIn ? role ?? null : null); // role이 undefined일 경우 null로 설정
//       })
//       .catch((error) => {
//         console.error("로그인 상태 확인 오류:", error);
//         setIsLoggedIn(false);
//         setRole(null);
//       });
//   }, []);

//   // ✅ 게시글 작성 핸들러
//   const handleSubmit = async () => {
//     if (!isLoggedIn) {
//       alert("로그인 후 게시글을 작성할 수 있습니다.");
//       router.push("/login");
//       return;
//     }

//     if (!title.trim()) {
//       alert("제목을 입력해주세요.");
//       return;
//     }
//     if (!content.trim()) {
//       alert("내용을 입력해주세요.");
//       return;
//     }

//     if (loading) return; // ✅ 중복 요청 방지
//     setLoading(true);

//     try {
//       const newPost: Post = await createPost({ title, content }); // ✅ 게시글 작성 API 호출
//       alert("게시글이 성공적으로 작성되었습니다.");
//       console.log("작성된 게시글:", newPost);
//       router.push("/board"); // ✅ 게시글 목록 페이지로 이동
//     } catch (error: any) {
//       console.error("게시글 작성 실패:", error);
//       const errorMessage =
//         error.response?.data?.message || error.message || "알 수 없는 오류가 발생했습니다.";
//       alert(`게시글 작성 중 오류가 발생했습니다: ${errorMessage}`);
//     } finally {
//       setLoading(false);
//     }
//   };


//   return (
//     <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
//     <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
//       <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>

//       <input
//         type="text"
//         placeholder="제목을 입력하세요"
//         value={title}
//         onChange={(e) => setTitle(e.target.value)}
//         className="w-full p-2 mb-4 border rounded"
//       />

//       <textarea
//         placeholder="내용을 마크다운 형식으로 작성하세요"
//         value={content}
//         onChange={(e) => setContent(e.target.value)}
//         className="w-full p-2 mb-4 border rounded flex-1"
//       />

//       <button
//         onClick={handleSubmit}
//         className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
//       >
//         작성하기
//       </button>
//     </div>

//          {/* 미리보기 */}
//          <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
//         <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
//         <div className="prose max-w-none p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
//           {content ? ( // 조건부 렌더링 
//             <ReactMarkdown 
//               remarkPlugins={[remarkGfm, remarkBreaks]} //줄바꿈 적용
//               rehypePlugins={[rehypeHighlight]} 
//             >
//               {content}
//             </ReactMarkdown>
//           ) : ( 
//             <p className="text-gray-400">내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.</p> // 기본 메시지 색상 조정
//           )}
//       </div>
//     </div>
//   </div>
// );
// };


// export default WritePostPage;







// "use client";
// import { useEffect, useState } from "react";
// import { useRouter } from "next/navigation";
// import axios from "axios";
// import ReactMarkdown from "react-markdown";
// import remarkGfm from "remark-gfm";
// import remarkBreaks from "remark-breaks";
// import rehypeHighlight from "rehype-highlight";
// import "highlight.js/styles/github.css";

// // ✅ 게시글 타입 정의
// interface Post {
//   id: number;
//   title: string;
//   content: string;
//   createdAt: string;
//   updatedAt?: string;
// }

// // ✅ 로그인 상태 응답 타입
// interface AuthStatusResponse {
//   isLoggedIn: boolean;
//   // role?: "user" | "admin";
// }

// const WritePostPage = () => {
//   const [title, setTitle] = useState("");
//   const [content, setContent] = useState("");
//   const [isLoggedIn, setIsLoggedIn] = useState(false);
//   const [role, setRole] = useState<string | null>(null);
//   const [loading, setLoading] = useState(false);
//   const router = useRouter();

//   // ✅ useEffect로 로그인 상태 확인 (쿠키 기반)
//   useEffect(() => {
//     axios
//       .get<AuthStatusResponse>("/api/v1/auth/status", { withCredentials: true })
//       .then((response) => {
//         console.log(response.data);
//         const { isLoggedIn, role } = response.data;
//         setIsLoggedIn(isLoggedIn);
//         setRole(role ?? null);
//       })
//       .catch((error) => {
//           if (error.response) {
//             console.error("❌ 로그인 상태 확인 실패:", error.response.data);
//           } else {
//             console.error("❌ 서버에 연결할 수 없습니다. 네트워크 상태를 확인하세요.");
//           }
//         setIsLoggedIn(false);
//         setRole(null);
//       });
//   }, []);

//   // ✅ 게시글 작성 함수 (쿠키 인증 포함)
//   // const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
//   //   const response = await axios.post<{ status: string; data: Post }>(
//   //     "/api/v1/user/boards",
//   //     postData,
//   //     { withCredentials: true }
//   //   );
//   //   return response.data.data;
//   // };
//   export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
//     const response = await api.post<RsData<Post>>('/user/boards', postData, {
//       withCredentials: true, // 로그인된 사용자 정보 자동 포함
//     });
//     return response.data.data;
//   };

//   // ✅ 게시글 작성 핸들러
//   const handleSubmit = async () => {
//     if (!isLoggedIn) {
//       alert("로그인 후 게시글을 작성할 수 있습니다.");
//       router.push("/login");
//       return;
//     }

//     if (!title.trim()) {
//       alert("제목을 입력해주세요.");
//       return;
//     }
//     if (!content.trim()) {
//       alert("내용을 입력해주세요.");
//       return;
//     }

//     if (loading) return; // ✅ 중복 요청 방지
//     setLoading(true);

//     try {
//       const newPost: Post = await createPost({ title, content }); // ✅ 게시글 작성 API 호출
//       alert("게시글이 성공적으로 작성되었습니다.");
//       console.log("작성된 게시글:", newPost);
//       router.push("/board"); // ✅ 게시글 목록 페이지로 이동
//     } catch (error: any) {
//       console.error("게시글 작성 실패:", error);
//       const errorMessage =
//         error.response?.data?.message || error.message || "알 수 없는 오류가 발생했습니다.";
//       alert(`게시글 작성 중 오류가 발생했습니다: ${errorMessage}`);
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
//       <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
//         <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>

//         <input
//           type="text"
//           placeholder="제목을 입력하세요"
//           value={title}
//           onChange={(e) => setTitle(e.target.value)}
//           className="w-full p-2 mb-4 border rounded"
//         />

//         <textarea
//           placeholder="내용을 마크다운 형식으로 작성하세요"
//           value={content}
//           onChange={(e) => setContent(e.target.value)}
//           className="w-full p-2 mb-4 border rounded flex-1"
//         />

//         <button
//           onClick={handleSubmit}
//           className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
//         >
//           작성하기
//         </button>
//       </div>

//       {/* 미리보기 */}
//       <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
//         <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
//         <div className="prose max-w-none p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
//           {content ? (
//             <ReactMarkdown 
//               remarkPlugins={[remarkGfm, remarkBreaks]} 
//               rehypePlugins={[rehypeHighlight]} 
//             >
//               {content}
//             </ReactMarkdown>
//           ) : ( 
//             <p className="text-gray-400">내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.</p>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default WritePostPage;


"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import remarkBreaks from "remark-breaks";
import rehypeHighlight from "rehype-highlight";
import "highlight.js/styles/github.css";
import { createPost } from "@/lib/board";


// ✅ 게시글 타입 정의
interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt?: string;
}

const WritePostPage = () => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  // ✅ 게시글 작성 핸들러
  const handleSubmit = async () => {
    if (!title.trim()) {
      alert("제목을 입력해주세요.");
      return;
    }
    if (!content.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }

    if (loading) return; // ✅ 중복 요청 방지
    setLoading(true);

    try {
      const newPost: Post = await createPost({ title, content }); // ✅ board.ts에서 가져온 createPost 사용
      alert("게시글이 성공적으로 작성되었습니다.");
      console.log("작성된 게시글:", newPost);
      router.push("/board"); // ✅ 게시글 목록 페이지로 이동
    } catch (error: any) {
      console.error("게시글 작성 실패:", error);
      const errorMessage =
        error.response?.data?.message || error.message || "알 수 없는 오류가 발생했습니다.";
      alert(`게시글 작성 중 오류가 발생했습니다: ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  };


  return (
    <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
      <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
        <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>

        <input
          type="text"
          placeholder="제목을 입력하세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full p-2 mb-4 border rounded"
        />

        <textarea
          placeholder="내용을 마크다운 형식으로 작성하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="w-full p-2 mb-4 border rounded flex-1"
        />

        <button
          onClick={handleSubmit}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
        >
          작성하기
        </button>
      </div>

      {/* 미리보기 */}
      <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
        <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
        <div className="prose max-w-none p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
          {content ? (
            <ReactMarkdown 
              remarkPlugins={[remarkGfm, remarkBreaks]} 
              rehypePlugins={[rehypeHighlight]} 
            >
              {content}
            </ReactMarkdown>
          ) : ( 
            <p className="text-gray-400">내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default WritePostPage;
