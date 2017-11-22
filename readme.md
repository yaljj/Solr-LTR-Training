# Solr-LTR-Training<br>
> ## 间接
  >项目用于为Apache Solr（7.10）训练排序学习模型。项目基于特定格式的原始数据生成用于排序学习训练的数据集后，利用**ranklib**对数据集进行训练生成模型参数文件，并将ranklib的模型格式转换为solr的模型格式。
  目前支持的模型为[org.apache.solr.ltr.model.MultipleAdditiveTreesModel](https://lucene.apache.org/solr/7_0_0//solr-ltr/org/apache/solr/ltr/model/MultipleAdditiveTreesModel.html)。
  项目自带的原始数据源于https://www.banggood.com 一个星期内的搜索记录数据和近一个月的商品特征数据。<br>
  

> ##数据文件描述和配置
  >下面将详细描述在现有数据[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)的情况下，
  进行lambdaMART模型训练和solr-ltr配置的具体流程和操作。<br> 
>### 商品特征配置文件<br>
  >特征配置文件为[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet),文件为json格式，用以定义特征。
  ```Java
  {  
    	"name": "productConfig",
    	"str_prop": ["product_name"],
    	"value_prop": ["product_id","cat_id","brand_id"],
    	"rank_feature": ["BM25","price","basket","pay_num","review","add_time"]
  }  
  ```
  >其中 "str_prop"用于设置字符串型的属性名，“value_prop”用于设置数值型且不作为ltr特征的属性名。
  上述二者仅用于作为document的field上传到solr。而"rank_feature"则是用于ltr计算的特征属性。上述例子中，特征包含：<br>
  >>**BM25**:关联性因子，solr中默认的原始得分为org.apache.solr.ltr.feature.OriginalScoreFeature。<br>
  >>**review**：商品评论数<br>
  >>**price**：商品价格<br>
  >>**pay_num**：商品30天内的销量<br>
  >>**basket**：商品30天内的加购量<br>
  >>**add_time**：商品30天内的加购量<br>
  >**注意**:若需要增减特征因子，可以在rank_feature中添加新的因子<br>
 >**注意**: 由于BM25是即时计算的，并非商品属性，因此若要采用其他关联度因子或者改变BM25计算的字符串对象，则需要修改BM25相关代码。<br>
  
>### 原始数据<br>
  >>原始数据在目录中[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)
  >>####prop.json
  >>>描述:prop.json存放商品的一般属性原始数据,其中product_id为唯一区分项<br>
  >>>数据格式:<br>
  ```Java
{"product_id":"7104","product_name":"12 Colors Acrylic Nail Art Tips Glitter Powder Dust","price":"5.78","add_time":"1507896522","cat_id":"1334","brand_id":"0"}
{"product_id":"7105","product_name":"500 White French Acrylic Half False Tips 3D Nail Art","price":"5.69","add_time":"1509783901","cat_id":"1327","brand_id":"0"}
{"product_id":"7157","product_name":"Acrylic UV Gel False Fake Nail Art Tips Clipper Manicure Cutter Tool","price":"3.76","add_time":"1507896522","cat_id":"1367","brand_id":"0"}
{"product_id":"7340","product_name":"5pcs 2 Way Nail Art Dotting Marbleizing Painting Pen","price":"2.25","add_time":"1507896522","cat_id":"1343","brand_id":"0"}
  ```
  >>>**注意**:原始数据属性名（如："product_id"）需要和FeatureConf.json中的属性名一致<br>
  >>>**注意**:可在此为商品添加新的特征项<br>
  
>### 商品<br>
  >>原始数据在目录中[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)
  >>####prop.json
  >>>描述:prop.json存放商品的一般属性原始数据,其中product_id为唯一区分项<br>
  >>>数据格式:<br>
  ```Java
{"product_id":"7104","product_name":"12 Colors Acrylic Nail Art Tips Glitter Powder Dust","price":"5.78","add_time":"1507896522","cat_id":"1334","brand_id":"0"}
{"product_id":"7105","product_name":"500 White French Acrylic Half False Tips 3D Nail Art","price":"5.69","add_time":"1509783901","cat_id":"1327","brand_id":"0"}
{"product_id":"7157","product_name":"Acrylic UV Gel False Fake Nail Art Tips Clipper Manicure Cutter Tool","price":"3.76","add_time":"1507896522","cat_id":"1367","brand_id":"0"}
{"product_id":"7340","product_name":"5pcs 2 Way Nail Art Dotting Marbleizing Painting Pen","price":"2.25","add_time":"1507896522","cat_id":"1343","brand_id":"0"}
  ```
  >>>**注意**:原始数据属性名（如："product_id"）需要和FeatureConf.json中的属性名一致<br>
  >>>**注意**:可在此为商品添加新的特征项<br>