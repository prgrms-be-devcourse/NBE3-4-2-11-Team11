import { useEffect } from "react";

interface PostcodeProps {
  onComplete: (address: string) => void;
}

const Postcode: React.FC<PostcodeProps> = ({ onComplete }) => {
  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    script.async = true;
    document.body.appendChild(script);
  }, []);

  const handlePostcode = () => {
    new (window as any).daum.Postcode({
      oncomplete: (data: { address: string }) => {
        onComplete(data.address);
      },
    }).open();
  };

  return (
    <button type="button" onClick={handlePostcode} className="p-2 bg-blue-500 text-white rounded whitespace-nowrap">
      검색
    </button>
  );
};

export default Postcode;
