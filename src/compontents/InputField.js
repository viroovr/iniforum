import React from "react";
import './styles/Login.css';

const InputField = ({label, ...props}) => (
    <div>
        <label>{label}</label>
        <input {...props}/>
    </div>
);

export default InputField;