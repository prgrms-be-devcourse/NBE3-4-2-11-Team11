"use client";

import Image from "next/image";
import useTokenRefresh from "@/utils/useTokenRefresh";

export default function Home() {

  // useTokenRefresh();  // ✅ 토큰 만료 체크 주기적 실행

  return (
    <div className="flex flex-col items-start bg-gray-100 text-gray-900 min-h-screen">

    {/* 첫 번째 섹션 - 헤더와 간격 추가 & 글 더 왼쪽으로 이동 */}
    <section className="w-full max-w-full mx-auto grid grid-cols-1 md:grid-cols-2 gap-24 items-center justify-start p-8 mt-32 pt-16 mb-16">
      {/* 왼쪽 텍스트 */}
      <div className="max-w-lg self-start pt-20">
        <h1 className="text-5xl font-bold text-black mb-4">POFO</h1>
        <p className="text-2xl font-semibold text-gray-800">
          <br />개발자의 지식과 경험을 한 곳에<br />
          당신만의 아카이브
        </p>
        <p className="mt-4 text-gray-600">
          <br /> <br />개발자들이 자신의 지식, 프로젝트, 그리고 창의적인 아이디어를 체계적으로
          관리할 수 있는 특별한 공간입니다.
        </p>
      </div>

      {/* 오른쪽 이미지 */}
      <div className="flex justify-center w-[680px] h-[500px] overflow-hidden rounded-lg shadow-lg">
        <Image
          src="https://i.postimg.cc/6pw120hB/main-image-1jpg.jpg"
          alt="PostImage 예제 이미지"
          width={900}
          height={600}
          className="w-full h-full object-cover"
          unoptimized
        />
      </div>
    </section>



    {/* 두 번째 섹션 */}
    {/* <section className="w-full max-w-6xl grid grid-cols-1 md:grid-cols-2 items-center justify-start p-8 mb-32"> */}
    {/* <section className="w-full max-w-full grid grid-cols-1 md:grid-cols-2 gap-48 items-center justify-start p-8 mb-32"> */}
    <section className="w-full max-w-full mx-auto grid grid-cols-1 md:grid-cols-2 gap-24 items-center justify-start p-8 mt-40 pt-16 mb-16">

      {/* 왼쪽 이미지 */}
      <div className="flex justify-start w-[600px] h-[450px] overflow-hidden rounded-lg shadow-lg">
        <Image
          src="https://i.postimg.cc/KzzVBv2X/download.jpg"
          alt="개발 기록 관련 이미지"
          width={700}
          height={450}
          className="w-full h-full object-cover"
          unoptimized
        />
      </div>

      {/* 오른쪽 텍스트 */}
      <div className="max-w-lg self-start pt-24">
        <h2 className="text-3xl font-bold text-black mb-4">효율적인 기록</h2>
        <p className="text-gray-600">
          코드 스니펫, 기술 노트, 문제 해결 등 개발 과정에서의 중요한 데이터를 한 곳에 저장하세요.
        </p>
        <h2 className="text-3xl font-bold text-black mt-6 mb-4">체계적인 분류</h2>
        <p className="text-gray-600">
          태그와 카테고리를 활용해 필요한 정보를 빠르게 검색할 수 있습니다.
        </p>
      </div>
    </section>



    {/* 세 번째 섹션 - 유지 */}
    <section className="w-full max-w-full mx-auto grid grid-cols-1 md:grid-cols-2 gap-32 items-center justify-self-end  mt-20 pt-16 mb-32 pl-32">

      {/* 왼쪽 텍스트 */}
      <div>
        <h2 className="text-3xl font-bold text-black mb-4">공유와 협업</h2>
        <p className="text-gray-600">
          기록된 내용을 공유하거나 협업 도구를 활용하여 개발 커뮤니티와 연결하세요.
        </p>
        <h2 className="text-3xl font-bold text-black mt-6 mb-4">지속적인 성장</h2>
        <p className="text-gray-600">
          과거의 경험을 토대로 발전하며, 자신만의 기술 스택과 노하우를 축적할 수 있습니다.
        </p>
      </div>

      {/* 세 번째 이미지 */}
      <div className="flex justify-center w-[600px] h-[450px] overflow-hidden rounded-lg shadow-lg">
      {/* <div className="flex justify-center w-[750px] h-[500px] overflow-hidden rounded-lg shadow-lg"> */}

      {/* <div className="flex justify-end w-full max-w-none h-[450px] overflow-hidden rounded-lg shadow-lg pr-0"> */}

      <Image
          src="https://i.postimg.cc/GpRsSH7D/3.jpg"
          alt="협업 및 성장 관련 이미지"
          width={400}
          height={500}
          className="w-full h-full object-cover"
          unoptimized
        />
      </div>
    </section>
  </div>
);
}