import {useState} from "react";
import {https_user} from "../../service/api";

export function AccountSetting({ user }) {
    const firstLetter = user.displayName ? user.displayName.charAt(0).toUpperCase() : "?";

    // Badge màu theo status
    const statusColor =
        user.status === "ACTIVE"
            ? "bg-green-100 text-green-800"
            : user.status === "INACTIVE"
                ? "bg-gray-100 text-gray-800"
                : "bg-[#FF7875] text-white"; // dùng #FF7875

    const [editName, setEditName] = useState(false);
    const [nameValue, setNameValue] = useState(user.displayName);
    const [tempName, setTempName] = useState(user.displayName);

    const handleEdit = () => setEditName(true);
    const handleCancel = () => {
        setTempName(nameValue);
        setEditName(false);
    };
    const handleUpdate = async () => {
        try{

            const res = await https_user.patch("accounts/me",{
                displayName: tempName,
            })

            if(res.status === 200) {
                alert(res.data.message);
            }

        } catch (e){
            console.log(e);
        }

        setNameValue(tempName);
        setEditName(false);
    };

    return (
        <div className="flex flex-col items-center p-6 bg-white rounded-lg shadow-sm space-y-4 h-full">
            {/* Avatar */}
            {user.avatar ? (
                <img
                    src={user.avatar}
                    alt="avatar"
                    className="w-28 h-28 rounded-full border border-gray-200 shadow-sm object-cover"
                />
            ) : (
                <div className="w-28 h-28 rounded-full border border-gray-200 shadow-sm bg-[#FF7875] flex items-center justify-center text-5xl font-bold text-white">
                    {firstLetter}
                </div>
            )}

            {/* Display Name Editable */}
            <div className="flex flex-col items-center space-y-2">
                {editName ? (
                    <>
                        <input
                            type="text"
                            value={tempName}
                            onChange={(e) => setTempName(e.target.value)}
                            className="border border-gray-300 rounded-md px-3 py-1 text-center text-lg"
                        />
                        <div className="flex space-x-2">
                            <button
                                onClick={handleUpdate}
                                className="px-3 py-1 bg-[#FF7875] text-white rounded-md hover:bg-[#e06666]"
                            >
                                Update
                            </button>
                            <button
                                onClick={handleCancel}
                                className="px-3 py-1 bg-gray-200 rounded-md hover:bg-gray-300"
                            >
                                Cancel
                            </button>
                        </div>
                    </>
                ) : (
                    <h2
                        className="text-2xl font-semibold cursor-pointer hover:text-[#FF7875]"
                        onClick={handleEdit}
                    >
                        {nameValue}
                    </h2>
                )}
            </div>

            {/* Email */}
            <p className="text-sm text-gray-500">{user.email}</p>

            {/* Role & Status Badges */}
            <div className="flex space-x-2">
        <span className="px-3 py-1 bg-indigo-100 text-indigo-800 text-xs font-medium rounded-full">
          {user.role}
        </span>
                <span className={`px-3 py-1 text-xs font-medium rounded-full ${statusColor}`}>
          {user.status}
        </span>
            </div>
        </div>
    );
}
