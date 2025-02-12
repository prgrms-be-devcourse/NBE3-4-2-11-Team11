"use client";

import { useState, useEffect } from 'react';
import { getPostById, updatePost } from '../../../../lib/board';
import { useRouter, useParams } from 'next/navigation';  // useParams 추가
import Header from '../../../../components/Header';
import { useAuthStore } from '@/store/authStore';
import { getAccessToken } from '@/utils/token';
import { decodeJWT } from '@/utils/decodeJWT';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

// Post 타입에 작성자 이메일 추가
interface Post {
  id: number;
  title: string;
  content: string;
  email: string;  // 작성자 이메일로 변경
}

// JWT 토큰에서 이메일 추출 함수
const extractEmailFromToken = (): string | null => {
  const token = getAccessToken();
  if (!token) return null;

  try {
    const decoded = decodeJWT(token) as { sub: string };  // 디코딩된 객체의 타입을 명확히 지정
    return decoded.sub || null;  // JWT의 sub 필드에 이메일 저장
  } catch (error) {
    console.error('토큰 디코딩 실패:', error);
    return null;
  }
};

const EditPostPage = () => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [postEmail, setPostEmail] = useState('');
  const router = useRouter();
  const params = useParams();  // useParams로 params 가져오기
  const { isLoggedIn } = useAuthStore();

  const loggedInEmail = extractEmailFromToken();  // 로그인한 사용자의 이메일 가져오기
  const postId = params.id as string;  // params.id는 문자열로 반환됨

  useEffect(() => {
    const fetchPost = async () => {
      const post = await getPostById(Number(postId));
      setTitle(post.title);
      setContent(post.content);
      setPostEmail(post.email);  // 게시글 작성자의 이메일 저장
    };
    fetchPost();
  }, [postId]);

  const handleSubmit = async () => {
    if (!isLoggedIn) {
      alert('로그인 후 게시글을 수정할 수 있습니다.');
      router.push('/login');
      return;
    }

    // 작성자 검증 (로그인한 이메일과 게시글 작성자의 이메일 비교)
    if (loggedInEmail?.toLowerCase() !== postEmail.toLowerCase()) {
      alert('작성자만 게시글을 수정할 수 있습니다.');
      return;
    }

    try {
      await updatePost(Number(postId), { title, content, email: loggedInEmail });
      // router.push(`/board/${postId}`);  // 수정 후 상세 페이지로 이동
      router.push(`/board`);  // 수정 후 목록 페이지로 이동
    } catch (error) {
      console.error('게시글 수정 실패:', error);
    }
  };

//   return (
//     <div className="min-h-screen bg-gray-100">
//       <Header />
//       <main className="max-w-xl mx-auto mt-8">
//         <h1 className="text-2xl font-bold mb-4">게시글 수정</h1>
//         <input
//           type="text"
//           value={title}
//           onChange={(e) => setTitle(e.target.value)}
//           className="w-full p-2 mb-4 border rounded"
//         />
//         <textarea
//           value={content}
//           onChange={(e) => setContent(e.target.value)}
//           className="w-full p-2 mb-4 border rounded h-40"
//         />
//         <button
//           onClick={handleSubmit}
//           className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
//         >
//           수정하기
//         </button>
//       </main>
//     </div>
//   );
// };

return (
  <div className="min-h-screen bg-gray-100 flex justify-center items-start p-8">
    <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md mr-4 h-[80vh] flex flex-col">
      <h1 className="text-2xl font-bold mb-4">게시글 수정</h1>

      <input
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="w-full p-2 mb-4 border rounded"
      />

      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        className="w-full p-2 mb-4 border rounded flex-1"
      />

      <button
        onClick={handleSubmit}
        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
      >
        수정하기
      </button>
    </div>

    <div className="w-1/2 max-w-xl bg-white p-6 rounded shadow-md h-[80vh] flex flex-col">
      <h2 className="text-2xl font-semibold mb-4">미리보기</h2>
      <div className="p-4 border rounded bg-gray-50 flex-1 overflow-y-auto">
        <ReactMarkdown remarkPlugins={[remarkGfm]}>
          {content || '내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.'}
        </ReactMarkdown>
      </div>
    </div>
  </div>
);
};


export default EditPostPage;
