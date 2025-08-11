# The Strategists - Monopoly-Inspired Multiplayer Game

[![Netlify Status](https://api.netlify.com/api/v1/badges/9fd07386-0ff5-4f26-97c7-503273b3f276/deploy-status)](https://app.netlify.com/sites/strategists/deploys)
[![Build and Push Docker Images](https://github.com/shubham1chawla/the-strategists-remastered/actions/workflows/publish.yml/badge.svg?branch=main)](https://github.com/shubham1chawla/the-strategists-remastered/actions/workflows/publish.yml)

The Strategists represents a sophisticated turn-based online multiplayer gaming platform designed to accommodate 2-6
participants in strategic competition. This gaming environment offers an immersive experience wherein players engage
in calculated decision-making within a structured virtual battleground.

The game features comprehensive gameplay mechanics that extend beyond conventional gaming parameters. Players have
the opportunity to acquire and manage properties across more than 20 Indian cities, which serve as strategic assets
rather than merely decorative board elements. The platform incorporates advanced visualization technology that
presents territorial development through graph-based representations, providing players with clear insights into
economic progression and territorial expansion.

The Strategists employs a dynamic economic framework that requires continuous adaptation and strategic planning.
This system necessitates ongoing analysis of market conditions and opponent strategies, thereby encouraging players
to develop sophisticated tactical approaches to maintain competitive advantage. Success in The Strategists requires
innovative thinking and strategic flexibility. The platform is designed to challenge participants through complex
decision-making scenarios while providing an engaging competitive environment. Players must demonstrate tactical
acumen, economic management skills, and strategic foresight to achieve victory within this comprehensive gaming
framework.

The game serves as a platform for strategic competition where participants can test their analytical abilities and
decision-making capabilities against other skilled players.

> [!TIP]
> Watch The Strategists' latest gameplay demo on [YouTube](https://www.youtube.com/watch?v=hvkbyQzbgW0).

## Technologies

![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=Vite&logoColor=white)
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)
![Redux](https://img.shields.io/badge/redux-%23593d88.svg?style=for-the-badge&logo=redux&logoColor=white)
![Ant-Design](https://img.shields.io/badge/-AntDesign-%230170FE?style=for-the-badge&logo=ant-design&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)
![NumPy](https://img.shields.io/badge/numpy-%23013243.svg?style=for-the-badge&logo=numpy&logoColor=white)
![Pandas](https://img.shields.io/badge/pandas-%23150458.svg?style=for-the-badge&logo=pandas&logoColor=white)
![Matplotlib](https://img.shields.io/badge/Matplotlib-%23ffffff.svg?style=for-the-badge&logo=Matplotlib&logoColor=black)
![scikit-learn](https://img.shields.io/badge/scikit--learn-%23F7931E.svg?style=for-the-badge&logo=scikit-learn&logoColor=white)
![mlflow](https://img.shields.io/badge/mlflow-%23d9ead3.svg?style=for-the-badge&logo=numpy&logoColor=blue)
![FastAPI](https://img.shields.io/badge/FastAPI-005571?style=for-the-badge&logo=fastapi)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Google Cloud](https://img.shields.io/badge/GoogleCloud-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white)
![Netlify](https://img.shields.io/badge/netlify-%23000000.svg?style=for-the-badge&logo=netlify&logoColor=#00C7B7)

## Features

This section covers the game's essential features, including the investments that allows players to purchase properties
in percentages, its effects on players’ net worth and properties market evaluations, and the game’s potential as an
educational tool in a classroom setting for explaining financial concepts like inflation and strategic investments.

### Login

The Strategists' login workflow uses _Google ReCAPTCHA_ to prevent bots from accessing the game. When you verify, the
game allows you to login using your _Google Account_, and presents you with _Create Game_ and _Join Game_ options. If
you choose to join a game, you must provide the four-digit alphabetic code associated with the game you wish to join.

|                                                                              |                                                                      |
| :--------------------------------------------------------------------------: | :------------------------------------------------------------------: |
| <img src="./images/login-1.png" /> (1) _Google ReCAPTCHA_ to verify the user |    <img src="./images/login-2.png" /> (2) _Google OAuth_ to login    |
|  <img src="./images/login-3.png" /> (3) Choose to _Create_ or _Join_ a game  | <img src="./images/login-4.png" /> (4) Provide a game's code to join |

> [!NOTE]
> Since the game is currently under-development and not available to general audience, only a handful of _Google Accounts_
> are allowed to create the game. Refer to the [Google Integration](./docs/google-integration.md#google-spreadsheets) to
> learn more about it.

### Game's Lobby

The focal point of gameplay revolves around a map showcasing all properties and the positions of each player. This map,
presented as a graph visualization, deliberately shows the conventional elements found in traditional maps, prioritizing
clarity and player movement direction through directed edges. Additionally, the game's accent color is different for each
player who joins the game for easier distinction.

When all the players join the game using the game code provided by the host player, you can see all the players starting
from the same position on the map. The host player can start the game, and who-so-ever gets the turn receives a prompt,
letting them know it's their turn to invest!

|                                                                                       |                                                                                                        |
| :-----------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------: |
| <img src="./images/lobby-1.png" /> (1) Game's lobby for player _Better's_ perspective | <img src="./images/lobby-2.png" /> (2) Player _Shubham-1's_ perspective when it's their turn to invest |

### Investments

Property investment is pivotal in enhancing players' net worth in The Strategists. Diverging from conventional board games,
where fixed property prices prevail, our game introduces a unique approach. Players can invest in property percentages,
thereby making it costlier for opponents. The act of investment triggers inflation in the property's market evaluation, and
the higher this evaluation, the more rent investors can collect. This distinctive feature adds an element of excitement,
compelling players to make strategic choices and invest judiciously, with the ultimate goal of amassing wealth.

|                                                                                       |                                                                                                     |
| :-----------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------: |
| <img src="./images/invest-1.png" /> (1) Player _Shubham's_ perspective when investing | <img src="./images/invest-2.png" /> (2) Property when player _Shubham-1_ has already invested in it |

### Map Interactions

Players can hover over properties to view their market evaluations, and can seamlessly zoom in or out, drag the map, and
click on players or properties to delve into their portfolios and investments.

|                                                                                                              |                                                                                                      |
| :----------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: |
| <img src="./images/tooltip-1.png" /> (1) Tooltip showing property _Lucknow's_ market value & total ownership | <img src="./images/tooltip-2.png" /> (2) Tooltip showing player _Shubham's_ net worth & in-hand cash |

### Players' & Properties' Statistics

When the game is underway, players can click on node on the map to reveal information about it. For player-based nodes,
you can see that player's change in the net worth and in-hand cash, called _Trends_, player's investments across the map,
called _Portfolio_, and, if predictions are enabled, player's change in winning probabilities, called _Predictions_. For
properties, you can see similar _Trends_ and _Investments_.

|                                                                  |                                                                    |
| :--------------------------------------------------------------: | :----------------------------------------------------------------: |
| <img src="./images/modal-1.png" /> (1) Player _Shubham's_ trends | <img src="./images/modal-2.png" /> (2) Player _Better's_ portfolio |

### Winner's Predictions

To enhance the gaming experience further, the game incorporates _Predictions_, providing players with insights into the
potential success of their current investment percentage. Leveraging past game data, these prompts offer a win probability
associated with the selected investment. This valuable information empowers new players to glean strategies employed by
previous game winners, enabling them to make more informed and strategic decisions. Integrating ML-driven feature adds a
layer of sophistication to the game, elevating the overall player experience and encouraging a more thoughtful and strategic
approach to property investment.

Players will get a notification at the end of each turn, letting everyone know who is most likely to win based on their
investment patterns. You will see these notifications flow in the _Activities_ tab, and through a chart when you click on
any player's node and navigate to the _Predictions_ tab.

|                                                                                                        |                                                                                                                           |
| :----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------: |
| <img src="./images/predictions-1.png" /> (1) Filtering games _Activities_ by _Predictions_ update type | <img src="./images/predictions-2.png" /> (2) Player _Better's Predictions_ tab showing change in their chances of winning |

### Player Ranking & Advices

When strategizing, players in the game can view other player's ranking, based on their net worths, in the _Lobby_ tab.
Additionally, if the advices are enabled, the game may offer players helpful tips based on their gameplay.

|                                                                                |                                                                                 |
| :----------------------------------------------------------------------------: | :-----------------------------------------------------------------------------: |
| <img src="./images/lobby.png" /> (1) The _Lobby_ tab showing player's rankings | <img src="./images/advices.png" /> (2) The _Advices_ tab for player _Shubham-1_ |

### Bankruptcy & Winning

When a player exhausts all their in-hand cash due to paying steep rents, they declare bankcruptcy, eliminating them from the
race of winning The Strategists. All their investments go back to the game, and invested properties market evaluation tanks,
allowing other players to buy them. The game ends when only player stands, and the last player left wins The Strategists!

All players can see winner's _Trends_, _Portfolio_, and _Predictions_, and the host player has an option to reset the game to
play another round with the same players in the lobby.

|                                                                                          |                                                                                         |
| :--------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------: |
| <img src="./images/bankruptcy.png" /> (1) Player _Better's_ perspective when bankrupted  |     <img src="./images/win-1.png" /> (2) Host player's perspective of _Trends_ tab      |
| <img src="./images/win-2.png" /> (3) Player _Shubham-1's_ perspective of _Portfolio_ tab | <img src="./images/win-3.png" /> (2) Player _Better's_ perspective of _Predictions_ tab |

## How to Play!

### On Netlify

Access The Strategists on [https://strategists.netlify.app/](https://strategists.netlify.app/).

> [!NOTE]
> Since the game is currently under-development and not available for GA, we frequently turn off the backend service to save
> server costs. If you want to play the game, please contact the developer for limitted time access or self-host.

### Self-host

Refer to the [self-hosting documentation](./docs/self-host.md) to play _The Strategists_ on your system.

## Blogs & Videos

- [How to write a messy useEffect in React and fix it (Medium Article)](https://medium.com/@shuchawl/how-to-write-a-messy-useeffect-in-react-and-fix-it-c0e98f872d22)
- [Leveraging Spring Aspect-Oriented Programming (AOP) for Event-Driven Updates (Medium Article)](https://medium.com/@shuchawl/leveraging-spring-aspect-oriented-programming-aop-for-event-driven-updates-a53240de6dc2)
- [From localhost to the cloud — my struggles with hosting The Strategists for free! (Medium Article)](https://medium.com/@shuchawl/from-localhost-to-the-cloud-my-struggles-with-hosting-the-strategists-for-free-66b7b5664c9a)
- [The Strategists | 1st Gameplay Demo | Monopoly-inspired Online Multiplayer Game (YouTube Video)](https://www.youtube.com/watch?v=hvkbyQzbgW0)
- [How did I use Google Sheets as my app’s free database? (Medium Article)](https://medium.com/@shuchawl/how-did-i-use-google-sheets-as-my-apps-free-database-c38faa5be6b3)
- [How did I use Google Drive as a free cloud storage solution to automate syncing my Machine Learning data? (Medium Article)](https://medium.com/@shuchawl/how-did-i-use-google-drive-as-a-free-cloud-storage-solution-to-automate-syncing-my-machine-learning-e1f288ebeab9)
- [Effects of an AI Intervention in a Financial Game Scenario (ASU Library)](https://hdl.handle.net/2286/R.2.N.198233)
