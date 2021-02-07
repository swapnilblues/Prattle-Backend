var ws;
var messages=[];
var language;
var username;
var to;




async function translate(text){
    let body= {
        "text": text,
        "target": language
    }
    console.log(body)
   return await fetch('https://prattletranslate.herokuapp.com/translate', {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            'content-type': 'application/json'
        }
    }).then(resp=>resp.text()).then(trans=>trans);
}


async function connect() {
     username = document.getElementById("username").value;
     language = document.getElementById('langs').value;

    const user = {
        "username":document.getElementById("username").value,
        "email":document.getElementById("email").value,
        "password":document.getElementById("password").value
    }
    const u = await fetch('https://prattle-java-app.herokuapp.com/rest/user/auth', {
        method: 'POST',
        body: JSON.stringify(user),
        headers: {
            'content-type': 'application/json'
        }
    })
    var host = document.location.host;
    var pathname = document.location.pathname;


    if(u.status!==500) {
        ws = new WebSocket("wss://" + host + pathname + "chat/" + username);
        ws.onmessage = async function (event) {
            var log = document.getElementById("log");
            var reply_log = document.getElementById("reply_log");
            var parentId = document.getElementById("message_id").value;

            console.log(event.data);
            var message = JSON.parse(event.data);

            if(message.id==parentId && message.id!==0){
                renderReplies(message,reply_log)
            }
            else{
                let flag = false;
                for(let i=0;i<messages.length;i++){
                    if(messages[i].id===message.id){
                        messages[i] = message;
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    messages.push(message);
                }
                console.log("Messages"+messages);

                if(message.content.length>15){
                    console.log(message.content)
                    message.content = CryptoJS.AES.decrypt(message.content, "Password").toString(CryptoJS.enc.Utf8);
                    message.content=await translate(message.content)
                    console.log(message)
                }


                if(message.containsGroup){
                    log.innerHTML += message.from.username + " to " + message.chatGroup.name + ": " + message.content + " id: " + message.id + "\n";

                }
                else {
                    if (message.to === null) {
                        log.innerHTML += message.from ? message.from.username : message.username + " to all : " + message.content + " id: " + message.id + "\n";

                    } else {
                        log.innerHTML +=
                            message.from.username + " to " + message.to.username + " : " + message.content + " id: " + message.id + "\n";
                    }

                }

            }

        };
    }
}
async function send() {
    console.log("SEND STARTED");
    var content = document.getElementById("msg").value;
    let encrypted = CryptoJS.AES.encrypt(content, "Password");
     to = document.getElementById("to").value;

    const attachment = document.getElementById("attachment").value;
    var json;
    if (to === ""){
        json = JSON.stringify({
            "content":encrypted.toString(),
            "attachment":attachment
        });
    }
    else {
        json = JSON.stringify({
            "content":encrypted.toString(),
            "to": {"username":to},
            "attachment":attachment
        });
        console.log("JSON "+json);
    }

    await ws.send(json);
}

async function sendToGroup() {
    var content = document.getElementById("msg").value;
    let encrypted = CryptoJS.AES.encrypt(content, "Password");
    var groupName = document.getElementById("toGroup").value;
    var groupPassword = document.getElementById("groupPassword").value;
    var json;

    if(groupPassword.trim().length > 0) {
        json = JSON.stringify({
            "content": encrypted.toString(),
            "chatGroup": {"name": groupName, "password": groupPassword}
        });
    }
    else {
        json = JSON.stringify({
            "content": encrypted.toString(),
            "chatGroup": {"name": groupName}
        });
    }

    await ws.send(json);
}



async function sendReplies(){
    to = document.getElementById("to").value;
    await fetch(`https://prattle-java-app.herokuapp.com/rest/message/getExchangedMessages/user1/${username}/user2/${to}`)
        .then(mesresp=>mesresp.json())
        .then(messagesresp=>{
            messages = messagesresp;
            console.log(messagesresp)
        });
    var reply = document.getElementById("reply_content").value;
    var parentId = document.getElementById("message_id").value;


    if(!(reply == "" && parentId== "")) {

        for(let i=0;i<messages.length;i++){
            console.log(messages[i]==parentId)
            if(messages[i].id==parentId){
                messages[i].from = messages[i].fromUser;
                messages[i].to = messages[i].toUser;
                delete messages[i].fromUser;
                delete messages[i].toUser;
                delete messages[i].timeStamp;
                for(let k=0;k<messages[i].replies.length;k++)
                {
                    messages[i].replies[k].from = messages[i].replies[k].fromUser;
                    messages[i].replies[k].to = messages[i].replies[k].toUser;
                    delete messages[i].replies[k].fromUser;
                    delete messages[i].replies[k].toUser;
                    delete messages[i].replies[k].timeStamp;

                }
                let rep = {
                    content:CryptoJS.AES.encrypt(reply, "Password").toString(),
                    replies:[],
                    to:{username:to},
                    from:{username:username}
                };
                messages[i].replies.push(rep);
                console.log(messages[i])
                ws.send(JSON.stringify(messages[i]))

            }
        }


    }
}

async function replyToGroup(){
    var groupName = document.getElementById("toGroup").value;
    await fetch(`https://prattle-java-app.herokuapp.com/rest/message/getExchangedMessages/group/${groupName}`)
        .then(mesresp=>mesresp.json())
        .then(messagesresp=>{
            messages = messagesresp;
            console.log(messagesresp)
        });
    var reply = document.getElementById("reply_content").value;
    var parentId = document.getElementById("message_id").value;


    if(!(reply == "" && parentId== "")) {

        for(let i=0;i<messages.length;i++){
            console.log(messages[i]==parentId)
            if(messages[i].id==parentId){
                messages[i].from = messages[i].fromUser;
                delete messages[i].fromUser;
                delete messages[i].toUser;
                delete messages[i].timeStamp;
                for(let k=0;k<messages[i].replies.length;k++)
                {
                    messages[i].replies[k].from = messages[i].replies[k].fromUser;
                    delete messages[i].replies[k].fromUser;
                    delete messages[i].replies[k].toUser;
                    delete messages[i].replies[k].timeStamp;

                }
                let rep = {
                    content:CryptoJS.AES.encrypt(reply, "Password").toString(),
                    replies:[],
                    chatGroup:{name:groupName},
                    from:{username:username}
                };
                messages[i].replies.push(rep);
                console.log(messages[i])
                ws.send(JSON.stringify(messages[i]))

            }
        }


    }
}


async function renderReplies(newMessages,replyLog) {
    let decrypted = await translate(CryptoJS.AES.decrypt(newMessages.content, "Password").toString(CryptoJS.enc.Utf8));
    replyLog.innerHTML+= decrypted+":\n";
    for(let i=0;i<newMessages.replies.length;i++){
        replyLog.innerHTML+= await translate(CryptoJS.AES.decrypt(newMessages.replies[i].content, "Password").toString(CryptoJS.enc.Utf8))+"\n";
    }
}





