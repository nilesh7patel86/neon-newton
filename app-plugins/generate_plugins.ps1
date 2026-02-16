$plugins = @(
    @{ Id="weather-plugin"; Name="WeatherPlugin"; Ext="WeatherExtension"; Title="Weather Widget"; Cat="Utilities"; Desc="Current LA Weather"; Icon="CLOUD"; Body='new Label("Los Angeles: 72F Sunny")' },
    @{ Id="stocks-plugin"; Name="StocksPlugin"; Ext="StocksExtension"; Title="Stock Ticker"; Cat="Finance"; Desc="Live market data"; Icon="SHOW_CHART"; Body='new Label("AAPL: $150.25 (+1.2%)")' },
    @{ Id="news-plugin"; Name="NewsPlugin"; Ext="NewsExtension"; Title="Tech News"; Cat="News"; Desc="Latest headlines"; Icon="RSS_FEED"; Body='new ListView<String>(javafx.collections.FXCollections.observableArrayList("Java 25 Released", "AI takes over coding"))' },
    @{ Id="dictionary-plugin"; Name="DictionaryPlugin"; Ext="DictionaryExtension"; Title="Dictionary"; Cat="Education"; Desc="Word of the Day"; Icon="MENU_BOOK"; Body='new Label("Serendipity: The occurrence of events by chance in a happy way.")' },
    @{ Id="translator-plugin"; Name="TranslatorPlugin"; Ext="TranslatorExtension"; Title="Translator"; Cat="Utilities"; Desc="Simple translations"; Icon="TRANSLATE"; Body='new Label("Hello -> Hola")' },
    @{ Id="unit-plugin"; Name="UnitPlugin"; Ext="UnitExtension"; Title="Unit Converter"; Cat="Utilities"; Desc="Metric to Imperial"; Icon="STRAIGHTEN"; Body='new Label("100km = 62.1 miles")' },
    @{ Id="ip-plugin"; Name="IpPlugin"; Ext="IpExtension"; Title="My IP Address"; Cat="Network"; Desc="Show public IP"; Icon="PUBLIC"; Body='new Label("192.168.1.105")' },
    @{ Id="sysinfo-plugin"; Name="SysInfoPlugin"; Ext="SysInfoExtension"; Title="System Info"; Cat="Utilities"; Desc="OS and Java version"; Icon="INFO"; Body='new Label(System.getProperty("os.name") + " - Java " + System.getProperty("java.version"))' },
    @{ Id="speed-plugin"; Name="SpeedPlugin"; Ext="SpeedExtension"; Title="Network Speed"; Cat="Network"; Desc="Upload/Download"; Icon="NETWORK_CHECK"; Body='new Label("Down: 450 Mbps | Up: 25 Mbps")' },
    @{ Id="dice-plugin"; Name="DicePlugin"; Ext="DiceExtension"; Title="Dice Roller"; Cat="Games"; Desc="Roll a d6"; Icon="CASINO"; Body='new Button("Roll: 5")' }
)

$grp = "com.neon.newton.plugin"
$ver = "1.0-SNAPSHOT"

foreach ($p in $plugins) {
    $id = $p.Id
    $pkgSuffix = $id.Replace("-plugin", "")
    $pkg = "$grp.$pkgSuffix"
    $path = "$id/src/main/java/" + $pkg.Replace(".", "/")
    $resPath = "$id/src/main/resources"
    
    # Create Directories
    New-Item -ItemType Directory -Force -Path $path | Out-Null
    New-Item -ItemType Directory -Force -Path $resPath | Out-Null
    
    # 1. POM.xml
    $pom = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.neon.newton</groupId>
        <artifactId>app-plugins</artifactId>
        <version>$ver</version>
    </parent>
    <groupId>$grp.$pkgSuffix</groupId>
    <artifactId>$id</artifactId>
    <version>$ver</version>
    <properties>
        <plugin.id>$id</plugin.id>
        <plugin.class>$pkg.$($p.Name)</plugin.class>
        <plugin.version>1.0.0</plugin.version>
        <plugin.provider>Neon</plugin.provider>
        <plugin.dependencies />
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.neon.newton</groupId>
            <artifactId>app-api</artifactId>
            <version>$ver</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-javafx</artifactId>
            <version>`${ikonli.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-material2-pack</artifactId>
            <version>`${ikonli.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Plugin-Id>`${plugin.id}</Plugin-Id>
                            <Plugin-Class>`${plugin.class}</Plugin-Class>
                            <Plugin-Version>`${plugin.version}</Plugin-Version>
                            <Plugin-Provider>`${plugin.provider}</Plugin-Provider>
                            <Plugin-Dependencies>`${plugin.dependencies}</Plugin-Dependencies>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
"@
    Set-Content -Path "$id/pom.xml" -Value $pom

    # 2. Plugin Class
    $pluginClass = @"
package $pkg;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
public class $($p.Name) extends Plugin {
    public $($p.Name)(PluginWrapper wrapper) { super(wrapper); }
}
"@
    Set-Content -Path "$path/$($p.Name).java" -Value $pluginClass

    # 3. Extension Class
    $extClass = @"
package $pkg;
import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class $($p.Ext) implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("$($p.Title)");
        title.setStyle("-fx-font-size: 24px;");
        Node content = $($p.Body);
        root.getChildren().addAll(title, content);
        return root;
    }
    @Override public String getMenuTitle() { return "$($p.Title)"; }
    @Override public Node getIcon() { return new FontIcon(Material2MZ.$($p.Icon)); }
    @Override public String getCategory() { return "$($p.Cat)"; }
    @Override public String getDescription() { return "$($p.Desc)"; }
    @Override public String getKeywords() { return "$($p.Title), $($p.Cat), plugin"; }
}
"@
    Set-Content -Path "$path/$($p.Ext).java" -Value $extClass

    # 4. plugin.properties
    $props = @"
plugin.id=$id
plugin.class=$pkg.$($p.Name)
plugin.version=1.0.0
plugin.provider=Neon
plugin.dependencies=
"@
    Set-Content -Path "$resPath/plugin.properties" -Value $props
    
    Write-Host "Generated $id"
}
