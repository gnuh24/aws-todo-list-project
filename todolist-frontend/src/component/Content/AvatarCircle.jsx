import ImageHelper from "../../helpers/ImageHelper";

/**
 * Render avatar circle
 * @param {string} avatar - URL ảnh
 * @param {string} name - tên user
 * @param {number} size - kích thước avatar
 * @returns React component
 */
export default function AvatarCircle({ avatar, name, size = 24 }) {
    const avatarUrl = ImageHelper.getAvatarUrl(avatar);
    console.log(avatarUrl);
    return (
        <div
            className="rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold flex-shrink-0"
            style={{ width: size, height: size }}
        >
            {avatarUrl ? (
                <img
                    src={avatarUrl}
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
