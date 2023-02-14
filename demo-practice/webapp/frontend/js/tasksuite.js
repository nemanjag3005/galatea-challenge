window.onmessage = (event) => {
    if (event.data) {
        try {
            let message = JSON.parse(event.data)
            console.log(message)
            if (message.command === "close") {
                //Perform actions that closes/destroys Iframe once the process is completed
            } else if (message.command === "loan_complete") {
                //Perform actions after customer accepts the loan
                if (message.data.approved && message.data.approved === true) {
                    document.querySelector('#tua-app-view').$server.onLoanComplete(message.data.loan_id);
                }
            } else if (message.command === "application_complete") {
                //Perform actions after customer is presented with options or is declined
                if (message.data.approved && message.data.approved === true) {
                    document.querySelector('#tua-app-view').$server.onApplicationApproved(message.data.application_id);
                }
                if (message.data.declined && message.data.declined === true) {
                    document.querySelector('#tua-app-view').$server.onApplicationDeclined(message.data.application_id);
                }
            }
        } catch (error) {
            //log error
            console.log(error)
        }
    }
}
