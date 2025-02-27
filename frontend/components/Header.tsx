"use client";

import Link from "next/link";
import {useEffect, useState} from "react";
import { useAuthStore } from "@/store/authStore";
import {useRouter} from "next/navigation";


const Header = () => {
  const router = useRouter();
  const { isLoggedIn, logout, checkAuthStatus } = useAuthStore();
  const [hasChecked, setHasChecked] = useState(false);  // ✅ 새로고침 방지용 상태 추가
  const [authState, setAuthState] = useState(isLoggedIn);

  useEffect(() => {
    if (!hasChecked) {
      checkAuthStatus();
      setHasChecked(true);
    }

    // ✅ "authChange" 이벤트 리스너 추가
    const syncAuthState = () => {
      //console.log("🔄 로그인 상태 변경 감지 → 상태 갱신");
      checkAuthStatus();
      setAuthState(useAuthStore.getState().isLoggedIn); // ✅ 상태 즉시 반영
    };

    window.addEventListener("authChange", syncAuthState);

    return () => {
      window.removeEventListener("authChange", syncAuthState);
    };
  }, [hasChecked]);

  const handleLogout = async () => {
    await logout();
    router.replace("/login"); // ✅ 로그아웃 후 login 페이지로 이동

//     setAuthState(false); // ✅ 상태 즉시 반영
  };


  return (
      <header className="bg-gray-900 text-white py-4 px-8 flex justify-between items-center">
        <div className="text-xl font-bold">
          <Link href="/" className="hover:text-gray-400">POFO</Link>
          {/*  POFO 로고 클릭시 메인페이지로 */}
        </div>
        <nav>
          <ul className="flex space-x-6">
            <li>
              <Link href="/notice" className="hover:text-gray-400">공지사항</Link>
            </li>
            <li className="relative group">
              <Link href="/mypage" className="hover:text-gray-400">마이페이지</Link>
              <ul className="absolute left-0 mt-2 w-32 bg-gray-800 text-white opacity-0 group-hover:opacity-100 transition-opacity">
                <li className="px-4 py-2 hover:bg-gray-700">
                  <Link href="/mypage/resume">이력서</Link>
                </li>
                <li className="px-4 py-2 hover:bg-gray-700">
                  <Link href="/mypage/projects">프로젝트</Link>
                </li>
                <li className="px-4 py-2 hover:bg-gray-700">
                  <Link href="/board">게시판</Link>
                </li>
              </ul>
            </li>
            <li>
              <Link href="/inquiry" className="hover:text-gray-400">문의하기</Link>
            </li>
            <li>
              {isLoggedIn ? (
                  <button onClick={handleLogout} className="hover:text-red-400">로그아웃</button>
              ) : (
                  <Link href="/login" className="hover:text-gray-400">로그인</Link>
              )}
            </li>

          </ul>
        </nav>
      </header>
  );
};

export default Header;