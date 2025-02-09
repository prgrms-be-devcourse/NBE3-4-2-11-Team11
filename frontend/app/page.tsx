import Image from "next/image";

export default function Home() {
  return (
    <div className="flex flex-col items-center bg-gray-100 text-gray-900 min-h-screen">
      <section className="w-full max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-8 items-center p-8">
        {/* 왼쪽 텍스트 */}
        <div>
          <h1 className="text-5xl font-bold text-black mb-4">POFO</h1>
          <p className="text-2xl font-semibold text-gray-800">
            개발자의 지식과 경험을 한 곳에, 당신만의 아카이브
          </p>
          <p className="mt-4 text-gray-600">
            개발자들이 자신의 지식, 프로젝트, 그리고 창의적인 아이디어를 체계적으로
            관리할 수 있는 특별한 공간입니다.
          </p>
        </div>

        {/* 오른쪽 이미지
        <div className="flex justify-center">
          <Image
            src="/developer.jpg" // public 폴더 안에 developer.jpg 이미지 추가 필요
            alt="개발자 이미지"
            width={600}
            height={400}
            className="rounded-lg shadow-lg"
          />
        </div> */}

        <div className="flex justify-center">
          <Image
            src="https://i.postimg.cc/6pw120hB/main-image-1jpg.jpg" //  PostImage Direct Link 사용
            alt="PostImage 예제 이미지"
            width={600}
            height={400}
            className="rounded-lg shadow-lg"
          />
        </div>
      </section>
    </div>
  );
}
