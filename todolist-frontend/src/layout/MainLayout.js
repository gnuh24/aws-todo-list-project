import Sidebar from "../component/Sidebar/Sidebar";

export default function MainLayout({ children }) {
  return (
    <div className="ml-64 flex-1 flex flex-col">
      <Sidebar />
      <div className="flex-1 bg-white">{children}</div>
    </div>
  );
}
