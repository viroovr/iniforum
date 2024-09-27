import React from "react";
import './styles/Login.css';

const ErrorMessage = ({message, className}) => {
    return message ? <p className={className}>message</p> : null;
};

export default ErrorMessage;