import axios from "axios";
import extractPublicIdFromUrl from "../extractPublicIdFromUrl";

const handleUpload = async (file, setUploadedImg) => {
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await axios.post('http://localhost:8080/api/v1/image/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        setUploadedImg(response.data.url);
    } catch (error) {
        console.error('Error:', error);
    }
};

const deleteImage = async (imageUrl) => {
    if (imageUrl) {
        try {
            const public_id = extractPublicIdFromUrl(imageUrl);
            await axios.delete(`http://localhost:8080/api/v1/image/delete/${public_id}`);
            console.log('Image deleted:', imageUrl);
        } catch (error) {
            console.error('Error deleting image:', error);
        }
    }
}
export {handleUpload,deleteImage}