// src/helpers/ImageHelper.js
import { BASE_URL } from "../service/api";

class ImageHelper {

    static getAvatarUrl(avatar) {
        if (!avatar) return null;


        // đã là URL tuyệt đối
        if (avatar.startsWith("http://") || avatar.startsWith("https://")) {
            return avatar;
        }


        return `${BASE_URL}/media/v1/local/${avatar}`;
    }

}

export default ImageHelper;
