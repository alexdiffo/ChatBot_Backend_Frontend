//Component pour les messages rapides
import React from 'react';
import './stylebot.css';


const QuickReply = (props) =>{
    return (
        <div className='quick'> 
        <div className='blod'>
        <b> You are welcome</b> <br/>
         Start a live chat 
        </div>
           
         <div >
         <a href='/' onClick={props.bool} > <img  style={{ height:40 , width:45 }} src={require("../../assets/next.png")} alt=''/> </a>
          </div>
        </div>
        
    )
}

export default QuickReply;