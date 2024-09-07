const Logout = () => {
    localStorage.removeItem('jwtToken');

    window.location.href = '/';
}

export default Logout;