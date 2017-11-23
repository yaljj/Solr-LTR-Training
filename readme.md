# Solr-LTR-Training<br>
## 简介
  >项目用于为Apache Solr（7.10）训练排序学习模型。项目基于特定格式的原始数据生成用于排序学习训练的数据集后，利用**ranklib**对数据集进行训练生成模型参数文件，并将ranklib的模型格式转换为solr的模型格式。
  目前支持的模型为[org.apache.solr.ltr.model.MultipleAdditiveTreesModel](https://lucene.apache.org/solr/7_0_0//solr-ltr/org/apache/solr/ltr/model/MultipleAdditiveTreesModel.html)。
  项目自带的原始数据源于https://www.banggood.com 一个星期内的搜索记录数据和近一个月的商品特征数据。<br>
  <br><br>
## Quick Start
>下面将详细描述在现有数据[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)的情况下，
进行MART模型训练和solr-ltr配置的具体流程和操作。<br> 


>### 构造训练集，测试集，验证集 <br>
> 运行脚本程序[Solr-LTR-Training/src/main/java/Main/SampleSetFactory](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Main/SampleSetFactory.java)。
该程序可更新Solr-LTR-Training/data/SampleSet下存放的训练集trainSet.txt，验证集VailiSet.txt，测试集testSet.txt。数据用于ranklib模型。
这些数据集的构造，基于原始数据[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)。
三个数据集的格式一致，随机等比例的进行分配。脚本执行的代码如下：<br>
```Java
SampleSetFactory.createSampleSet();
```
>数据集格式如下所示：<br>
```Java
1.6666666666666665 0 1:0.052396521256776483 2:0.13374604874102394 3:0.5227324654138016 4:0.30751221714520194 5:0.0 6:0.8661299198332197
6.0 0 1:0.05388126843863542 2:0.21648865808128998 3:0.43786728585006884 4:0.2384830647363997 5:0.07789122848925072 6:0.732263157721875
3.6 0 1:0.06491879482663733 2:0.08029717416530516 3:0.5155211937446508 4:0.4131687522256421 5:0.2587490602637329 6:0.9433190286166003
2.25 0 1:0.057118366297761736 2:0.15670786934272418 3:0.6853757184226908 4:0.6270966778889606 5:0.27923713326906724 6:0.9016873920030167
```
>在第一行中，1.6666666666666665表示搜索词与商品间的匹配度（类似与线性回归中的Y值），计算基于[Solr-LTR-Training/data/OriginalData/keyword_product_pair.txt]中的属性。
具体的属性含义在数据文件描述中说明。匹配度的计算在文件[Solr-LTR-Training/src/main/java/Calculation/ValueCalculation.java]（https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Calculation/ValueCalculation.java）中。
第二个值，“0”为关键词的标号，取决于关键词在[Solr-LTR-Training/data/OriginalData/keywords.txt](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/data/OriginalDataSet/keywords.txt)中的为位置；
“1:0.052396521256776483”指特征标号和对应的权重。特征标号所对应的特征可参照特征配置文件[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中"rerank_feature"数组的特征顺序,标号为1就是指"rerank_feature"数组中的第一个特征。<br>

> ### 训练MART模型 <br>
>利用[ranklib](https://sourceforge.net/p/lemur/wiki/RankLib/)训练模型。在根目录下执行命令：<br>
```Java
java -jar ./ranklib-2.3/bin/RankLib.jar -train ./data/SampleSet/trainSet.txt -test ./data/SampleSet/testSet.txt -validate ./data/SampleSet/valiSet.txt -ranker 6 -metric2t NDCG@10 -metric2T ERR@10 -save ./model/MART.txt
```
>部分参数描述:<br>
>>**-train <文件名>** ：调用训练集<br>
>>**-test <文件名>** ：调用测试集<br>
>>**-validate <文件名>** ：调用验证集<br>
>>**-metric2t**: 模型测试方法<br>
>>**-save <文件名>**: 模型保存路径<br>
>>**-ranker [1~8]** : 选择模型，ranklib支持8类LTR模型，模型名与对应标号如下：<br>
>>>MART (Multiple Additive Regression Trees, a.k.a. Gradient boosted regression tree) [6]<br>
>>>RankNet [1] <br>
>>>RankBoost [2]<br>
>>>AdaRank [3]<br>
>>>Coordinate Ascent [4]<br>
>>>LambdaMART [5]<br>
>>>ListNet [7]<br>
>>>Random Forests [8]<br>
><br>
><br>
>训练后,查看MART.txt文件:
```Java
## LambdaMART
## No. of trees = 1000
## No. of leaves = 10
## No. of threshold candidates = 256
## Learning rate = 0.1
## Stop early = 100

<ensemble>
	<tree id="1" weight="0.1">
		<split>
			<feature> 4 </feature>
			<threshold> 0.34135595 </threshold>
			<split pos="left">
				<feature> 2 </feature>
				<threshold> 0.1963398 </threshold>
				<split pos="left">
					<output> 0.3290064334869385 </output>
				</split>
				<split pos="right">
					<output> -0.8738656044006348 </output>
				</split>
			</split>
			<split pos="right">
				<feature> 3 </feature>
.....
.....
</ensemble>
```
>模型文件为xml格式。而solr的模型格式是json而且分布和参数命名都与ranklib训练出来的结果有一定的差别，因此还需要进行模型格式的转换。为此，专门写了一个solr模型转换的脚本程序。

>### ranklib模型转solr模型<br>
>运行脚本程序[Solr-LTR-Training/src/main/java/Main/SolrModelFactory.java](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Main/SolrModelFactory.java)。所执行的代码如下:<br>
```Java
SolrModelFactory mf = new SolrModelFactory(); //初始化对象
mf.storeMultipleAdditiveTrees("model/MART.txt","model/MART.json");//调用storeMultipleAdditiveTrees函数，"model/MART.txt"为ranklib训练的模型文件，"model/MART.json"为转换后的solr模型文件
```
>[MART.json](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/model/MART.json)格式如下所示：<br>


## 数据文件描述
下面是关于项目中部分文件的描述，若需增减特征，或者改变特征的命名，需要详细阅读以下内容
>#### 特征配置文件FeatureConf<br>
>特征配置文件[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)为json格式，用以定义特征。
定义的特征将用于利用原始数据的属性，产生ranklib训练的数据集[Solr-LTR-Training/data/SampleSet](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/SampleSet)（验证集，训练集，测试集）
  ```Java
  {  
    	"name": "productConfig",
    	"str_prop": ["product_name"],
    	"value_prop": ["product_id","cat_id","brand_id"],
    	"rank_feature": ["BM25","price","basket","pay_num","review","add_time"]
  }  
  ```
>其中 "str_prop"用于设置字符串型的属性名，“value_prop”用于设置数值型且不作为ltr特征的属性名。
上述二者作为document的field上传到solr，但不用于ltr排序。而"rank_feature"则是用于ltr计算的特征属性。上述例子中，特征包含：<br>
>>**BM25**:关联性因子，solr中默认的原始得分为org.apache.solr.ltr.feature.OriginalScoreFeature。<br>
>>**review**：商品评论数<br>
>>**price**：商品价格<br>
>>**pay_num**：商品30天内的销量<br>
>>**basket**：商品30天内的加购量<br>
>>**add_time**：商品30天内的加购量<br>
>**注意**:若需要增减特征因子，可以在rank_feature中添加新的因子<br>
>**注意**: 由于BM25是即时计算的，并非商品属性，因此若要采用其他关联度因子或者改变BM25计算的字符串对象，则需要修改BM25相关代码。<br>
><br>

>#### 原始数据文件<br>
>原始数据在目录[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中,
包含了prop.json,complex.json,keyword.txt,keyword_product_pair.txt等文件,下面介绍相关文件内容和作用<br>
>##### 商品一般属性（prop.json）<br>
>描述:prop.json存放商品的一般属性原始数据,其中product_id为唯一区分项。数据格式如下:<br>
  ```Java
{"product_id":"7104","product_name":"12 Colors Acrylic Nail Art Tips Glitter Powder Dust","price":"5.78","add_time":"1507896522","cat_id":"1334","brand_id":"0"}
{"product_id":"7105","product_name":"500 White French Acrylic Half False Tips 3D Nail Art","price":"5.69","add_time":"1509783901","cat_id":"1327","brand_id":"0"}
{"product_id":"7157","product_name":"Acrylic UV Gel False Fake Nail Art Tips Clipper Manicure Cutter Tool","price":"3.76","add_time":"1507896522","cat_id":"1367","brand_id":"0"}
{"product_id":"7340","product_name":"5pcs 2 Way Nail Art Dotting Marbleizing Painting Pen","price":"2.25","add_time":"1507896522","cat_id":"1343","brand_id":"0"}
  ```
>**注意**:属性名（如："product_id"）需要特征配置文件[FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中的属性名一致<br>
>**注意**:可在此为商品添加新的特征项<br>
><br>
><br>

>##### 商品统计属性(complex.json)<br>
>描述:complex.json存放商品的统计属性，例如一定时间内的销售量，加够量等,其中product_id为唯一区分项。数据格式如下：<br>
  ```Java
{"product_id":"18576","basket":"0.0","review":"0","pay_num":"0.0"}
{"product_id":"18589","basket":"36.0","review":"135","pay_num":"7.0"}
{"product_id":"18599","basket":"0.0","review":"10","pay_num":"0.0"}
  ```
>**注意**:属性名（如："basket"）需要特征配置文件[FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中的属性名一致<br>
>**注意**:可在此为商品添加新的特征项<br>
><br>
><br>
>##### 搜索词-商品对(keyword_product_pair.txt)<br>
>描述:keyword_product_pair.txt存放搜索词以及商品的关系，例如在第一行中,squishy为搜索词，1153352为商品id,14是一个月内搜索词下点击该商品的uv，6是一个月内该搜索词下加够该商品的数量，4一个月内是该搜索词下该商品的销量。数据格式如下：<br>
  ```Java
	squishy`1153352`14`6`4`
	squishy`1113507`18`10`3`
	squishy`1181645`23`13`3`
	squishy`1122654`6`3`3`
	squishy`1160930`35`8`3`
	squishy`1145181`19`17`3`
	squishy`1120879`42`10`3`
	squishy`1168577`30`17`3`
  ```
><br>
><br>
  
>##### 搜索词(keywords.txt)<br>
>描述:keywords.txt存放keyword_product_pair.txt中包含的搜索词<br>
><br>
><br>