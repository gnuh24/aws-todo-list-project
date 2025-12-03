import { useState } from "react";
import { Copy, X } from "lucide-react";

export default function ShareModal({ onClose, onSelectUser }) {
  const [email, setEmail] = useState("");

  const handleAddEmail = () => {
    if (email.trim() === "") return;

    if (onSelectUser) onSelectUser(email.trim());
    setEmail("");
  };

  return (
    <>
      <div className="fixed top-16 right-6 w-[480px] h-[70vh] bg-white rounded-xl shadow-2xl overflow-y-auto z-50 animate-slideIn">
        <button
          className="absolute top-3 right-3 text-gray-500 hover:text-gray-700"
          onClick={onClose}
        >
          <X size={20} />
        </button>

        <div className="p-6">
          <input
            type="text"
            placeholder="Add people by name or email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleAddEmail()}
            className="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-gray-300"
          />

          {/* suggestion */}
          {email && (
            <div
              className="mt-3 p-4 bg-white rounded-xl shadow border cursor-pointer hover:bg-gray-50"
              onClick={handleAddEmail}
            >
              <div className="text-sm font-semibold mb-3">
                Invite someone new
              </div>

              <div className="flex items-center gap-3 p-2 border rounded-lg bg-gray-50">
                <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
                <span className="text-sm">{email}</span>
              </div>
            </div>
          )}

          <div className="mt-8 flex justify-center">
            <img
              src="https://todoist.b-cdn.net/assets/images/78a3e5a4d8ddc37d.png"
              alt="illustration"
              className="w-64 opacity-90"
            />
          </div>

          <div className="text-center mt-4 text-sm text-gray-700">
            <div className="font-semibold">
              Collaborate with friends and family
            </div>
            <p className="mt-1 text-gray-600">
              Invite others to finally get on top of those household chores or
              plan that dream holiday.
            </p>
          </div>

          <div className="flex justify-between items-center border-t mt-6 pt-4">
            <button className="flex items-center gap-2 text-sm text-gray-600 hover:underline">
              <span>❓</span> Learn about sharing
            </button>

            <button className="flex items-center gap-2 text-sm border px-3 py-2 rounded-md hover:bg-gray-100">
              <Copy size={16} /> Copy link
            </button>
          </div>
        </div>
      </div>

      <style>
        {`
          @keyframes slideIn {
            from { opacity: 0; transform: translateX(40px); }
            to   { opacity: 1; transform: translateX(0); }
          }
          .animate-slideIn {
            animation: slideIn 0.25s ease-out forwards;
          }
        `}
      </style>
    </>
  );
}
