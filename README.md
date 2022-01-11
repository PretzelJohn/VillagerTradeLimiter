<h1>VillagerTradeLimiter (VTL)</h1>
<h6>by PretzelJohn</h6>

<h2>Description:</h2>
<p>This Minecraft plugin limits the villager trade deals that players can get.</p>
<p>Click <a href="https://www.spigotmc.org/resources/87210/">here</a> to see this plugin on Spigot.</p>
<br>

<h2>Commands:</h2>
<table>
    <tr>
        <th>Command</th>
        <th>Alias</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>/villagertradelimiter</code></td>
        <td><code>/vtl</code></td>
        <td>Shows a help message</td>
    </tr>
    <tr>
        <td><code>/villagertradelimiter reload</code></td>
        <td><code>/vtl reload</code></td>
        <td>Reloads config.yml</td>
    </tr>
    <tr>
        <td><code>/villagertradelimiter see &lt;player&gt;</code></td>
        <td><code>/vtl see &lt;player&gt;</code></td>
        <td>Shows the trades for another player.<br/>To use, look at the villager you want<br/>to see the trades for.</td>
    </tr>
    <tr>
        <td><code>/villagertradelimiter invsee</code></td>
        <td><code>/vtl invsee</code></td>
        <td>Shows the inventory of a villager. <br/>To use, look at the villager you want<br/>to see the trades for.</td>
    </tr>
</table><br>

<h2>Permissions:</h2>
<table>
    <tr>
        <th>Permission</th>
        <th>Description</th>
        <th>Default User(s)</th>
    </tr>
    <tr>
        <td>villagertradelimiter.*</td>
        <td>Allows players to use ALL commands</td>
        <td>OP</td>
    </tr>
    <tr>
        <td>villagertradelimiter.use</td>
        <td>Allows players to use <code>/vtl</code></td>
        <td>OP</td>
    </tr>
    <tr>
        <td>villagertradelimiter.reload</td>
        <td>Allows players to reload config.yml and messages.yml</td>
        <td>OP</td>
    </tr>
    <tr>
        <td>villagertradelimiter.see</td>
        <td>Allows players to see the trades for another player<br/>with <code>/vtl see &lt;player&gt;</code></td>
        <td>OP</td>
    </tr>
    <tr>
        <td>villagertradelimiter.invsee</td>
        <td>Allows players to see inventory of a villager<br/>with <code>/vtl invsee</code></td>
        <td>OP</td>
    </tr>
</table><br>

<h2>Config:</h2>
<ul>
    <li>
        <p>Global settings: These settings apply to all villagers and villager trades.</p>
        <table>
            <tr>
                <th>Setting</th>
                <th>Description</th>
            </tr>
            <tr>
                <td><code>bStats:</code></td>
                <td>This helps me keep track of what server versions are being used. Please leave this set to true.</td>
            </tr>
            <tr>
                <td><code>database.mysql:</code></td>
                <td>Whether to use MySQL for the database (true) or SQLite (false)</td>
            </tr>
            <tr>
                <td><code>database.host:</code></td>
                <td>The IP address or domain name of your MySQL server. If the MySQL database is on the same server as your Minecraft server, leave this as <code>127.0.0.1</code></td>
            </tr>
            <tr>
                <td><code>database.port:</code></td>
                <td>The port number of your MySQL server. Usually <code>3306</code>.</td>
            </tr>
            <tr>
                <td><code>database.database:</code></td>
                <td>The name of your MySQL database, or schema. You must create a database (schema) before using this plugin!</td>
            </tr>
            <tr>
                <td><code>database.username:</code></td>
                <td>The username to access your MySQL database.</td>
            </tr>
            <tr>
                <td><code>database.password:</code></td>
                <td>The password to access your MySQL database.</td>
            </tr>
            <tr>
                <td><code>database.encoding:</code></td>
                <td>If your MySQL database uses an encoding other than <code>utf8</code>, change this.</td>
            </tr>
            <tr>
                <td><code>database.useSSL:</code></td>
                <td>If your MySQL database can use SSL connections, set this to <code>true</code>!</td>
            </tr>
            <tr>
                <td><code>DisableTrading:</code></td>
                <td>Whether to disable all villager trading for all worlds, some worlds, or no worlds.<br/><strong>Options:</strong>
                    <ul>
                        <li>Add world names for worlds that you want to completely disable ALL villager trading.</li>
                        <li>Set to true to disable trading in all worlds.</li>
                        <li>Set to false or [] to disable this feature.</li>
                    </ul>
                </td>
            </tr>
            <tr>
                <td><code>MaxHeroLevel:</code></td>
                <td>The maximum level of the "Hero of the Village" (HotV) effect that a player can have. This limits HotV price decreases.<br/><strong>Options:</strong>
                    <ul>
                        <li>Set to -1 to disable this feature and keep vanilla behavior</li>
                        <li>Set to a number between 0 and 5 to set the maximum HotV effect level players can have</li>
                    </ul>
                </td>
            </tr>
            <tr>
                <td><code>MaxDiscount:</code></td>
                <td>The maximum discount (%) you can get from trading/healing zombie villagers. This limits reputation-based price decreases.<br/><strong>Options:</strong>
                    <ul>
                        <li>Set to -1.0 to disable this feature and keep vanilla behavior</li>
                        <li>Set to a number between 0.0 and 1.0 to limit the maximum discount a player can get. (NOTE: 30% = 0.3)</li>
                        <li>Set to a number above 1.0 to increase the maximum discount a player can get. (NOTE: 250% = 2.5)</li>
                    </ul>
                </td>
            </tr>
            <tr>
                <td><code>MaxDemand:</code></td>
                <td>The maximum demand for all items. This limits demand-based price increases.<br/><strong>Options:</strong>
                    <ul>
                        <li>Set to -1 to disable this feature and keep vanilla behavior</li>
                        <li>Set to 0 or higher to set the maximum demand for all items</li>
                    </ul><br>
                    <strong>WARNING:</strong> The previous demand information cannot be recovered if it was higher than the MaxDemand.
                </td>
            </tr>
            <tr>
                <td><code>MaxUses:</code></td>
                <td>The maximum number of times a player can make any trade before a villager is out of stock.<br/><strong>Options:</strong>
                    <ul>
                        <li>Set to -1 to disable this feature and keep vanilla behavior</li>
                        <li>Set to 0 or higher to change the maximum number of uses for all items</li>
                    </ul>
                </td>
            </tr>
        </table>
    </li>
    <li>
        <p>Per-item settings: (<code>Overrides:</code>)</p>
        <table>
            <tr>
                <th>Setting</th>
                <th>Description</th>
            </tr>
            <tr>
                <td><code>&lt;item_name&gt;:</code></td>
                <td>Override the global settings by adding as many of these as you need. Enchanted books must follow the format of <code>name_level</code> (mending_1). All other items must follow the format of <code>item_name</code> (stone_bricks).</td>
            </tr>
            <tr>
                <td><code>.Disabled:</code></td>
                <td>Disables any trade that contains the item (true/false)</td>
            </tr>
            <tr>
                <td><code>.MaxDiscount:</code></td>
                <td>Sets the maximum discount for this item (-1.0, or between 0.0 to 1.0)</td>
            </tr>
            <tr>
                <td><code>.MaxDemand:</code></td>
                <td>Sets the maximum demand for this item (-1, or 0+)</td>
            </tr>
            <tr>
                <td><code>.MaxUses:</code></td>
                <td>Sets the maximum number of times a player can make the trade before the villager is out of stock</td>
            </tr>
            <tr>
                <td><code>.Cooldown:</code></td>
                <td>Sets the time between restocks for the trade, and applies to ALL villagers. Once the player reaches the <code>MaxUses</code>, the cooldown begins. The trade is disabled for all villagers until the cooldown expires.<br><strong>FORMAT:</strong> &lt;Number&gt;&lt;interval&gt;<br><strong>EXAMPLE:</strong> 30s = 30 seconds, 5m = 5 minutes, 4h = 4 hours, 7d = 7 days</td>
            </tr>
            <tr>
                <td><code>.Item1.Material:</code><br><code>.Item2.Material:</code><br><code>.Result.Material:</code></td>
                <td>Sets the material of the 1st or 2nd item in the trade<br><strong>WARNING:</strong> This cannot be undone!</td>
            </tr>
            <tr>
                <td><code>.Item1.Amount:</code><br><code>.Item2.Amount:</code><br><code>.Result.Amount:</code></td>
                <td>Sets the amount of the 1st or 2nd item in the trade<br><strong>WARNING:</strong> This cannot be undone!</td>
            </tr>
        </table>
    </li>
    <li>
        <p>For the default config.yml, see: <code>src/main/resources/config.yml</code></p>
    </li>
</ul>