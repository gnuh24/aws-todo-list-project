// src/helpers/ImageHelper.js
import React from "react";

class ImageHelper {
    /**
     * Render avatar circle
     * @param {string} avatar - URL ảnh
     * @param {string} name - tên user
     * @param {number} size - kích thước avatar
     * @returns React component
     */
    static AvatarCircle({ avatar, name, size = 24 }) {
        return (
            <div
                className="rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold flex-shrink-0"
                style={{ width: size, height: size }}
            >
                {avatar ? (
                    <img
                        src={avatar}
                        alt="avatar"
                        className="w-full h-full object-cover rounded-full"
                        onError={(e) => (e.currentTarget.src = "/default-avatar.png")}
                    />
                ) : (
                    name?.[0]?.toUpperCase() || "U"
                )}
            </div>
        );
    }
}

export default ImageHelper;
