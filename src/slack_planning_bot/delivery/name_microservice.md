#name-microservice
App should connect with name [name-microservice](https://github.com/makimo/name-microservice).

##usage
Function getting Jira user id and return connected Slack id for provided Jira user id.

##connection
To connect with name-microservice you just need to set up env url in project.clj.

##return
Function can return following values:

* **String** - return user is everything was fine.
* **nil** - if everything is fine, but not able to return user id from datebase.
* **:server-error** if server return status 400.
* **:bad-request** if server return status 404.
* **:unauthorized** if server return status 500.
* **:unexpected** if server return unhandled error.
* **:id-not-valid** if provided argument isn't string.
