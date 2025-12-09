export default function NotFound() {
  return (
    <div
      className="w-full h-screen bg-black cursor-pointer"
      onClick={() => (window.location.href = "/")}
    >
      <img
        src="https://cdn.prod.website-files.com/65ba70a5bb6f912baf0094a3/692588c50754ada9941bfd11_glenncatteeuw.com_404(1440).webp"
        alt="404"
        className="w-full h-full object-cover object-right"
      />
    </div>
  );
}
