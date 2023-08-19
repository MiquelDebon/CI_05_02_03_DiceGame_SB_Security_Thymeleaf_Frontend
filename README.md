
<p float="left">
  <img src="src/main/resources/readme/readmeImages/springSecurity.png" width="100" />
  <img src="src/main/resources/readme/readmeImages/mysql.png" width="100" />
  <img src="src/main/resources/readme/readmeImages/thymeleaf.png" width="100" />
</p>

## Spring Boot MySQL, Security, Thymeleaf

### Description
The dice game is played with two dice. If the result of the sum of the two dice is 7, the game is won, if not lost. A player can see a list of all the rolls he/she has made and the percentage of success.

To be able to play the game and make a roll, a user must register with a non-repeated name. Upon creation, it is assigned a unique numeric identifier and a registration date. If the user so wishes, you can not add a name and it will be called "ANONYMOUS". There can be more than one "ANONYMOUS" player.
Each player can see a list of all the rolls they have made, with the value of each die and whether or not they have won the game. In addition, you can know your success rate for all the rolls you have made.

You cannot delete a specific game, but you can delete the entire list of runs for a player.

The software must be able to list all the players in the system, the success percentage of each player and the average success percentage of all the players in the system.

The software must respect the main design patterns.

**URL's** 

    Frontend: /page

    - GET:    /home                     depending"hasAuthority('USER', 'ADMIN')"
    - GET:    /login    ?error ?logout
    - GET:    /register ?error
    - GET:    /players                  "isAthenticated()"

    Backend:  /players

    - POST:   /players                  -> Create a player.
    - PUT     /players                  -> change the name of the player.
    - POST    /players/{id}/games/      -> A specific player rolls the dice.
    - DELETE  /players/{id}/games       -> deletes the player's rolls.
    - GET     /players/                 -> returns the list of all the players in the system with their average success rate.
    - GET     /players/{id}/games       -> returns the list of games for a player.
    - GET     /players/ranking          -> returns the average ranking of all players in the system. That is, the average percentage of successes.
    - GET     /players/ranking/loser    -> returns the player with the worst success rate.
    - GET     /players/ranking/winner   -> returns the player with the worst success rate.


<table>
 <tr>
    <td><p>Home page not authenticated</p></td>
    <td><p>Home page authenticated as a Admin</p></td>
 </tr>
 <tr>
    <td> 
    <img src="src/main/resources/readme/screenShotProject/home_notAuthorize.png" alt="Italian 404">
    </td>
    <td>
    <img src="src/main/resources/readme/screenShotProject/home_admin.png" alt="Italian 500">
    </td>
 </tr>

 <tr>
    <td><p>Register</p></td>
    <td><p>Log in</p></td>
 </tr>
 <tr>
    <td> 
    <img src="src/main/resources/readme/screenShotProject/register.png" alt="Italian 404">
    </td>
    <td>
    <img src="src/main/resources/readme/screenShotProject/login.png" alt="Italian 500">
    </td>
 </tr> 

<tr>
    <td><p>Authenticated user - Access to play and current ranking</p></td>
    <td><p>Log out</p></td>
 </tr>
 <tr>
    <td> 
    <img src="src/main/resources/readme/screenShotProject/athenticated_players.png" alt="Italian 404">
    </td>
    <td>
    <img src="src/main/resources/readme/screenShotProject/logout.png" alt="Italian 500">
    </td>
 </tr>






</table>



### References:
- [spring-boot-spring-security-thymeleaf-example](https://mkyong.com/spring-boot/spring-boot-spring-security-thymeleaf-example/)


