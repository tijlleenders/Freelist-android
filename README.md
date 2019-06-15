# freelist-android
Native android todo app for freelist.

## Freelist goal
Freelist gives you the peace of mind to live in the now by making sure that:
* your life is on track with your priorities/goals
* your planning calendar is realistic
* you won't forget anything important

See https://github.com/tijlleenders/Freelist-android/wiki/Vision for more ideas

### Freelist design principles
* (F) Free
* (E) Easy
* (SS) Simple & Safe

#### Free
* F.1 100% financed by donations. 
* F.2 No ads. Not now, not ever - period.
* F.3 Free as in zero cost to download and use in app stores*. 
* F.4 Free as in open source.
###### \* Freelist is not developed/published for iOS as they have a fundamental incompatibility with GPLv3. The Free Software Foundation doesn't want GPLv3 apps in Apple Store (as Apple adds additional restrictions), and vice-versa.

#### Easy 
* E.1 One person in a team/family/group can use Freelist as their primary todo-tool without negatively affecting the others, even if they use a different tool. 
* E.2 No tutorials up-front, but as-you-go: when buttons/actions are first used a quick explanation is shown and connected preferences can be set after using the feature a few times (not on first use). 
* E.3 Fast touch/click feedback (<100ms) so cause-effect connection between user action and interface response is clear. 
* E.4 Undo button always present (and clickable as much as possible) so users are not afraid to make mistakes. Redo not implemented as it complicates user understanding and backend. 
* E.5 No complicated config menu's but a simple list with clear explanation/example of settings.
* E.6 Non-intrusive tips/reminders on features the user never seems to use. 
* E.7 Suggestions for more efficient way when app detects user doing same (inefficient) thing more than x times. 
* E.8 Internet or gps not required (design for offline first as not everyone has internet/gps all the time - by choice or necessity).
* E.9 Scalable so app does not become slow or  unusable when user has lots of data or when many users use it. 
* E.10 No ads as these negatively affect user experience (and introduce security risks). 

#### Simple & Safe
* SS1. Stick to the goal, and only the goal. 
Like Linux bash commands, Freelist should do one thing, and do it well: help you figure out what to do and when. For example, is not necessary to re-invent document or image storage/editing tools, just point to or integrate with the one the user prefers, if this is needed at all. 
* SS2. Favor simplicity over complexity in the user interface as well as in the code base. 
* SS3. End-to-end encryption (TLS + encrypted storage sufficient pfor current features (single user), if necessary implement an open source end-to-end encryotion protocok (ie signal protocol) at a later time). 
* SS4. Privacy by design and by default.
Freelist does not want or need to know who you are. You don't require a phone number or email to register. Even better: you don't even have to register at all if you prefer because you can use the app purely offline or sync with your own servers instead of the official Freelist servers (To make this possible whilst guaranteeing negative user experience, the quality of your own servers is monitored by the app so you will get a suggestion to switch to freelist servers if your own private servers negatively affect performance). This resolves the concern some other open source app maintainers have about allowing you to use your  own private servers (ie Signal chat app). 
* SS5. If you want to help improve Freelist you can turn on pseudonimized user statistics. This option is turned off by default.
* SS6. No ads as these introduce security risks (and  negatively affect user experience). 


## How to contribute?
https://opensource.guide/how-to-contribute/
... and read code of conduct and FAQ's.
