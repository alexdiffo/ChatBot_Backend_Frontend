
import React from "react"

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import './stylebot.css';

const Auth = () => {

    const[adminUsername,namechange]=useState("");
    const[adminPassword,passwordchange]=useState("");

    const navigate=useNavigate();

    const handlesubmit=(e)=>{
        e.preventDefault();
        const empdata={adminUsername,adminPassword};

        console.log('format',empdata);

    fetch("http://localhost:8080/api/login",{
        method:"POST",
        headers:{"content-type":"application/json"},
        body:JSON.stringify(empdata)
      }).then((res)=>{
        if(res.status == "200"){
          alert('Connecté avec success.')
          navigate('/admin');
        }else{
          alert('Mot de passe incorrect')
        }
       
      }).catch((err)=>{
        console.log(err.message)
      })
    }

    return (
        <div className="Auth-form-container">
          <form className="Auth-form" onSubmit={handlesubmit}>
            <div className="Auth-form-content">
              <h3 className="Auth-form-title">Administrator Access</h3>
              <div className="form-group mt-3">
                <label>Username</label>
                <input
                  type="name"
                  value={adminUsername} onChange={e=>namechange(e.target.value)}
                  className="form-control mt-1"
                  placeholder="enter username"
                />
              </div>
              <div className="form-group mt-3">
                <label>Password</label>
                <input
                  type="password"
                  value={adminPassword} onChange={e=>passwordchange(e.target.value)}
                  className="form-control mt-1"
                  placeholder="enter password"
                />
              </div>
              <div className="d-grid gap-2 mt-3">
                <button type="submit" className="btn btn-primary">
                  Submit
                </button>
              </div>
            </div>
          </form>
        </div>
      )
}

export default Auth;