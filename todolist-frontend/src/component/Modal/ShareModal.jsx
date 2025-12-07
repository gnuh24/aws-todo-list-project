import { useState, useEffect } from "react";
import { Copy, X } from "lucide-react";
import { https_user } from "../../service/api";

export default function ShareModal({ onClose, onSelectUser }) {
  const [email, setEmail] = useState("");
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);

  // ⏳ Debounce tìm email 400ms
  useEffect(() => {
    if (!email) {
      setUser(null);
      return;
    }

    const delay = setTimeout(() => {
      fetchUserByEmail(email);
    }, 400);

    return () => clearTimeout(delay);
  }, [email]);

  const fetchUserByEmail = async (emailValue) => {
    try {
      setLoading(true);

      const res = await https_user.get(`/v1/accounts?email=${emailValue}`);
console.log(res)
      // Nếu API trả về đúng format
      if (res.data?.status === 200 && res.data?.data) {
        setUser(res.data.data); // object user
      } else {
        setUser(null);
      }
    } catch (err) {
      console.error("Error fetching user:", err);
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectUser = () => {
    if (onSelectUser && user) {
      onSelectUser(user);
    }
    setEmail("");
    setUser(null);
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

          {/* Input email */}
          <input
            type="text"
            placeholder="Add people by name or email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-gray-300"
          />

          {/* Loading */}
          {loading && (
            <div className="mt-3 text-sm text-gray-500">Searching...</div>
          )}

          {/* Nếu tìm thấy user */}
          {user && !loading && (
            <div
              className="mt-3 p-4 bg-white rounded-xl shadow border cursor-pointer hover:bg-gray-50"
              onClick={handleSelectUser}
            >
              <div className="flex items-center gap-3 p-2 border rounded-lg bg-gray-50">
                <div className="w-10 h-10 bg-gray-200 rounded-full"></div>
                <div>
                  <div className="text-sm font-semibold">
                    {user.displayName || "Unknown user"}
                  </div>
                  <div className="text-xs text-gray-500">{user.email}</div>
                </div>
              </div>
            </div>
          )}

          {/* Không tìm thấy user */}
          {!loading && email && !user && (
            <div className="mt-3 p-4 bg-white rounded-xl border shadow">
              <div className="text-sm text-gray-600">No user found</div>
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
