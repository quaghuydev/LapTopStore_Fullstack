import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const ProtectedRoute = ({ component: Component, ...rest }) => {
    const [isAdmin, setIsAdmin] = useState(false);
    const token = localStorage.getItem('accessToken');
    const navigate = useNavigate();

    useEffect(() => {
        const checkAdminPermission = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/v1/auth/checkPermission', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setIsAdmin(response.data);
            } catch (error) {
                console.error('Error checking admin permission:', error);
                setIsAdmin(false);
            }
        };

        if (token) {
            checkAdminPermission();
        } else {
            setIsAdmin(false);
            navigate('/404');
        }
    }, [token]);

    if (!isAdmin) {
        navigate('/404');
    }

    return <Component {...rest} />;
};

export default ProtectedRoute;