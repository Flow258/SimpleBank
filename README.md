# SimpleBank - Minecraft Bank System Plugin

A comprehensive bank system plugin for Minecraft 1.21.5 that integrates with Vault economy plugins.

## Features

### Core Banking System
- **Bank Balance Management**: Players can deposit, withdraw, and check their bank balances
- **Vault Integration**: Works seamlessly with economy plugins like EssentialsX, CMI, Jobs, etc.
- **Secure Storage**: Bank balance stored separately from wallet balance

### Commands
- `/bank balance` - Check your bank balance
- `/bank deposit <amount|all>` - Deposit money into bank
- `/bank withdraw <amount|all>` - Withdraw money from bank
- `/bank help` - Show help message

### Admin Commands
- `/bank balance <player>` - Check another player's balance
- `/bank set <player> <amount>` - Set a player's bank balance
- `/bank reset <player>` - Reset a player's bank balance to $0
- `/bank top` - View top bank balances

### Advanced Features
- **Interest System**: Optional daily interest on bank accounts
- **Balance Limits**: Configurable maximum bank balance per player
- **Login Notifications**: Players are notified of their balance when joining
- **Auto-Save**: Automatic data saving to prevent loss
- **API Support**: Other plugins can interact with SimpleBank

## Installation

### Prerequisites
- Minecraft Server 1.21.5
- Java 17 or higher
- Vault plugin
- Economy plugin (EssentialsX, CMI, etc.)

### Setup Steps

1. **Download Dependencies**
   ```bash
   # Download Vault from SpigotMC
   # Download an economy plugin (EssentialsX recommended)
   ```

2. **Build the Plugin**
   ```bash
   git clone <your-repo>
   cd SimpleBank
   mvn clean package
   ```

3. **Install Plugin**
   - Copy `target/SimpleBank-1.0.0.jar` to your server's `plugins/` folder
   - Restart your server

4. **Configure Plugin**
   - Edit `plugins/SimpleBank/config.yml` to your preferences
   - Restart server or use `/reload`

## Configuration

### Basic Settings
```yaml
bank:
  max-balance: -1  # -1 for unlimited
  starting-balance: 0.0

interest:
  enabled: false
  rate: 0.01  # 1% per day
  minimum-balance: 1000.0
  interval-minutes: 1440  # 24 hours
```

### Message Customization
All messages are fully customizable in the config.yml file with color code support.

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `bank.use` | Use basic bank commands | `true` |
| `bank.deposit` | Deposit money | `true` |
| `bank.withdraw` | Withdraw money | `true` |
| `bank.balance` | Check bank balance | `true` |
| `bank.admin` | Admin commands | `op` |
| `bank.admin.set` | Set player balances | `op` |
| `bank.admin.reset` | Reset player balances | `op` |
| `bank.interest.exempt` | Exempt from interest | `false` |

## Data Storage

### YAML (Default)
- Simple file-based storage
- Good for small-medium servers
- Data stored in `plugins/SimpleBank/bankdata.yml`

### Future Database Support
- SQLite support planned
- MySQL support planned
- Automatic migration tools

## API Usage

Other plugins can interact with SimpleBank using the API:

```java
// Check if API is available
if (BankAPI.isAvailable()) {
    // Get player's bank balance
    double balance = BankAPI.getBankBalance(player);
    
    // Add money to bank
    BankAPI.addToBankBalance(player, 1000.0);
    
    // Check if player has enough money
    if (BankAPI.hasBankBalance(player, 500.0)) {
        BankAPI.removeFromBankBalance(player, 500.0);
    }
}
```

## Commands & Usage Examples

### Player Commands
```bash
# Check your balance
/bank balance

# Deposit $1000
/bank deposit 1000

# Deposit all money from wallet
/bank deposit all

# Withdraw $500
/bank withdraw 500

# Withdraw all money from bank
/bank withdraw all
```

### Admin Commands
```bash
# Check Steve's balance
/bank balance Steve

# Set Steve's balance to $5000
/bank set Steve 5000

# Reset Steve's balance to $0
/bank reset Steve

# View top 10 richest bank accounts
/bank top
```

## Development

### Project Structure
```
src/main/java/com/yourname/simplebank/
├── SimpleBank.java              # Main plugin class
├── api/
│   └── BankAPI.java            # API for other plugins
├── commands/
│   └── BankCommand.java        # Command handler
├── data/
│   └── DataManager.java        # Data storage
├── economy/
│   └── BankManager.java        # Bank logic
├── listeners/
│   └── PlayerListener.java     # Event handling
├── tasks/
│   └── InterestTask.java       # Interest system
└── utils/
    └── MessageUtils.java       # Message utilities
```

### Building from Source
```bash
git clone <repository-url>
cd SimpleBank
mvn clean package
```

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Support

### Common Issues

**"Disabled due to no Vault dependency found!"**
- Install Vault plugin and an economy plugin
- Make sure both plugins load before SimpleBank

**Commands not working**
- Check permissions in your permissions plugin
- Verify Vault is connecting to your economy plugin

**Data not saving**
- Check file permissions in plugins/SimpleBank/
- Look for errors in server console

### Getting Help
- Check the wiki for detailed guides
- Open an issue on GitHub for bugs
- Join our Discord for support

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Changelog

### Version 1.0.0
- Initial release
- Basic banking system
- Vault integration
- Interest system
- Admin commands
- Configurable messages
- API support
