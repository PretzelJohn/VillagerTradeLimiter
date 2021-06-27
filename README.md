<h1>VillagerTradeLimiter (VTL)</h1>
<h6>by PretzelJohn</h6>

<h2>Description:</h2>
<p>This Minecraft plugin limits the villager trade deals that players can get when they cure a zombie villager.</p>
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
        <td>shows a help message</td>
    </tr>
    <tr>
        <td><code>/villagertradelimiter reload</code></td>
        <td><code>/vtl reload</code></td>
        <td>reloads config.yml</td>
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
        <td>Allows players to use <code>/vtl</code> and <code>/vtl reload</code></td>
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
                <td><code>DisableTrading:</code></td>
                <td>Set this to true if you want to completely disable ALL villager trading.</td>
            </tr>
            <tr>
                <td><code>MaxHeroLevel:</code></td>
                <td>The maximum level of the "Hero of the Village" (HotV) effect that a player can have. This limits HotV price decreases. Options:
                    <ul>
                        <li>Set to -1 to disable this feature and keep vanilla behavior</li>
                        <li>Set to a number between 0 and 5 to set the maximum HotV effect level players can have</li>
                    </ul>
                </td>
            </tr>
            <tr>
                <td><code>MaxDiscount:</code></td>
                <td>The maximum discount (%) you can get from trading/healing zombie villagers. This limits reputation-based price decreases. Options:
                    <ul>
                        <li>Set to -1.0 to disable this feature and keep vanilla behavior</li>
                        <li>Set to a number between 0.0 and 1.0 to set the maximum discount a player can get. (NOTE: 30% = 0.3)</li>
                    </ul>
                </td>
            </tr>
            <tr>
                <td><code>MaxDemand:</code></td>
                <td>The maximum demand for all items. This limits demand-based price increases. Options:
                    <ul>
                        <li>Set to -1 to disable this feature and keep vanilla behavior</li>
                        <li>Set to 0 or higher to set the maximum demand for all items</li>
                    </ul><br>
                    WARNING: The previous demand information cannot be recovered if it was higher than the MaxDemand.
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
                <td><code>.MaxDiscount:</code></td>
                <td>Sets the maximum discount for this item</td>
            </tr>
            <tr>
                <td><code>.MaxDemand:</code></td>
                <td>Sets the maximum demand for this item</td>
            </tr>
        </table>
    </li>
    <li>
        <p>For the default config.yml, see: <code>src/main/resources/config.yml</code></p>
    </li>
</ul>