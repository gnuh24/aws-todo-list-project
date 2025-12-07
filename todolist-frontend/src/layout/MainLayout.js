import Sidebar from "../component/Sidebar/Sidebar";

export default function MainLayout({ children }) {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1 bg-white">{children}</div>
    </div>
  );
}
