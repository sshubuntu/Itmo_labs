import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

df = pd.read_csv("C:\labs\informatic\Lab5\Lab5ThirdTask.csv")

df = pd.melt(df, id_vars="Date",value_vars=["Opening Price","Maximum Price","Minimum Price","Closing Price"], value_name="Price", var_name="Position")

sns.boxplot(x="Date", y="Price", data=df, hue = "Position")

plt.xticks(fontsize=12, rotation=45)  
plt.yticks(fontsize=12)  
plt.title("Boxplot of the stock prices", fontsize=22, fontweight='bold', color='darkblue')
plt.tight_layout() 

plt.show()