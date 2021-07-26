
var ii = 
document.getElementById("hela") 
 var oo = 
 document.getElementById("btnn") 
var txt = 
document.getElementsByTagName("h2")
var txxt = 
document.getElementsByTagName("p")
oo.addEventListener( 
   "click",function(){
       ii.classList.toggle( "darkk" )
       for( 
           let i=0 ; i<txt.length ; i++ 
       )
       { 
           txt[i].classList.toggle( "light-textt" )
       }
       for( 
        let i=0 ; i<txxt.length ; i++ 
    )
    { 
        txxt[i].classList.toggle( "light-textt" )
    }
       
   } 
)
console.log(txt)

