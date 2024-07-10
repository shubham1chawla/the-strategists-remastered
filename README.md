# The Strategists - Monopoly-Inspired Multiplayer Game

[![Netlify Status](https://api.netlify.com/api/v1/badges/9fd07386-0ff5-4f26-97c7-503273b3f276/deploy-status)](https://app.netlify.com/sites/strategists/deploys)

Welcome to The Strategists—an exhilarating journey into turn-wise online multiplayer gaming! Brace yourself for an immersive experience like no other as you step into a virtual battleground where 8-10 players clash in a symphony of wit and strategy. Unveiling captivating gameplay, this game transcends the ordinary, inviting you to dive headfirst into a universe where every move is a calculated risk.

But hold on, there's more to it than meets the eye. Picture this: over 20 Indian cities are waiting to be conquered, not as mere spaces on the board, but as powerful properties you can strategically invest in. Witness the rise and fall of empires through a graph-like visualization that brings these lands to life. The game doesn't just stop there—it introduces a dynamic economic system that will keep you on your toes, forcing you to adapt, evolve, and outsmart your opponents.

In The Strategists, thinking outside the box isn't just a suggestion; it's a necessity. Prepare to be challenged, thrilled, and utterly captivated as you embark on a quest to conquer, command, and emerge victorious in this extraordinary gaming arena. The battlefield awaits—let the strategic spectacle begin!

> [!TIP]
> Watch The Strategists' latest gameplay demo on [YouTube](https://www.youtube.com/watch?v=hvkbyQzbgW0).

## Technologies

![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
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
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Google Cloud](https://img.shields.io/badge/GoogleCloud-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white)
![Netlify](https://img.shields.io/badge/netlify-%23000000.svg?style=for-the-badge&logo=netlify&logoColor=#00C7B7)

## Gameplay

This section covers the game's essential features, including the investment model that allows players to purchase properties in percentages, the effects of natural and manufactured events on players’ net worth and properties market evaluations, and the game’s potential as an educational tool in a classroom setting.

### Map Visualization

![The Strategists Map](/images/map.png)

The focal point of gameplay revolves around a map showcasing all properties and the positions of each player. This map, presented as a graph visualization, deliberately eschews the conventional elements found in traditional maps, prioritizing clarity and player movement direction through directed edges.

In the earlier version of the game, HTML Canvas was employed to create the visualization, albeit with limited interactivity. Players could merely hover over properties to view their market evaluations. However, the current development phase utilizes the [JavaScript library Cytoscape](https://js.cytoscape.org/), introducing a host of new interactions. Players can now seamlessly zoom in or out, drag the map, and click on players or properties to delve into their portfolios and investments.

![The Strategists Tooltip](/images/tooltip.png)

A notable improvement is the addition of interactive graph nodes, allowing players to access pertinent information with a simple hover. This includes details such as the current market evaluation of a property or the current net worth of any player. This enhancement provides players with a more comprehensive understanding of the game dynamics.

Regarding property investment, The Strategists diverge from traditional board game norms. Instead of fixed-price property acquisitions, players can strategically invest in percentages, making it costlier for competitors. Investing significantly inflates a property's market evaluation, leading to higher rent collection for the investors. This innovative feature injects excitement into the game, compelling players to make astute decisions and invest judiciously. The potential for increased earnings through strategic investments adds additional complexity, fostering a dynamic and engaging gameplay experience.

### Investments

![The Strategists Investments](/images/invest.png)

Property investment is pivotal in enhancing players' net worth in The Strategists. Diverging from conventional board games, where fixed property prices prevail, our game introduces a unique approach. Players can invest in property percentages, thereby making it costlier for opponents. The act of investment triggers inflation in the property's market evaluation, and the higher this evaluation, the more rent investors can collect. This distinctive feature adds an element of excitement, compelling players to make strategic choices and invest judiciously, with the ultimate goal of amassing wealth.

In the previous version of the game, players utilized a standard HTML text field to input their desired investment percentage. The latest game version introduces a slider with a helpful tooltip, elevating the investing experience. Players can now easily select their proposed investment with these user-friendly tools, presented on cards adorned with icons consistent with the game's theme.

### Winner Prediction

![The Strategists Predictions](/images/prediction.png)

To enhance the gaming experience further, the game incorporates prediction notifications, providing players with insights into the potential success of their current investment percentage. Leveraging past game data, these prompts offer a win probability associated with the selected investment. This valuable information empowers new players to glean strategies employed by previous game winners, enabling them to make more informed and strategic decisions. Integrating AI-driven notifications adds a layer of sophistication to the game, elevating the overall player experience and encouraging a more thoughtful and strategic approach to property investment.

### Portfolio

In the realm of portfolios and investments within the game, players can gain valuable insights into their fellow participants' strategies and property market evaluation trends. Traditionally, the game has employed tables to present this information on the player's dashboard.

![The Strategists Portfolio](/images/portfolio.png)

To enhance the user experience and provide a more intuitive grasp of investment records, the game now employs a Bubble Chart. This dynamic visualization method encodes information about property ownership and purchase amounts, offering players a more visually stimulating and insightful representation of their investments. The Bubble Chart allows players to discern distribution patterns at a glance.

![The Strategists Trends](/images/trends.png)

In addition to ownership interactions, the game visually represents how players' net worth and cash flow evolve with each turn. This trend analysis provides a comprehensive overview of financial dynamics, empowering players to make informed decisions as Area and Line visualization.

While the implementation of chance cards is not yet realized in this version, the potential for future integration is recognized. The game remains dynamic and responsive to player feedback, with ongoing enhancements to deliver a more immersive and strategic investment experience.

### Winner

![The Strategists Winner](/images/winner.png)

The game includes a confetti animation once a player wins. The prediction model trains itself after each game session, learning from every game.

## Setup

If you want to run the application on your local machine using Docker, use the `docker-compose up` command. Please make sure all the configurations are set before running this command. Refer to individual projects' README to read more.

## Blogs & Videos

- [How to write a messy useEffect in React and fix it](https://medium.com/@shuchawl/how-to-write-a-messy-useeffect-in-react-and-fix-it-c0e98f872d22)
- [Leveraging Spring Aspect-Oriented Programming (AOP) for Event-Driven Updates](https://medium.com/@shuchawl/leveraging-spring-aspect-oriented-programming-aop-for-event-driven-updates-a53240de6dc2)
- [From localhost to the cloud — my struggles with hosting The Strategists for free!](https://medium.com/@shuchawl/from-localhost-to-the-cloud-my-struggles-with-hosting-the-strategists-for-free-66b7b5664c9a)
- [The Strategists | 1st Gameplay Demo | Monopoly-inspired Online Multiplayer Game](https://www.youtube.com/watch?v=hvkbyQzbgW0)
- [How did I use Google Sheets as my app’s free database?](https://medium.com/@shuchawl/how-did-i-use-google-sheets-as-my-apps-free-database-c38faa5be6b3)
- [How did I use Google Drive as a free cloud storage solution to automate syncing my Machine Learning data?](https://medium.com/@shuchawl/how-did-i-use-google-drive-as-a-free-cloud-storage-solution-to-automate-syncing-my-machine-learning-e1f288ebeab9)
