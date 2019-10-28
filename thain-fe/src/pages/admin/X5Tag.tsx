import React, { useState } from 'react';
import { Tag, Input, Icon } from 'antd';
import { X5ConfigModel } from './models/X5ConfigModel';

interface TagState {
  principals: string[];
  setTag: React.Dispatch<React.SetStateAction<X5ConfigModel>>;
}

const X5Tag: React.FC<TagState> = ({ setTag, principals }) => {
  const [inputValue, setInputValue] = useState<string>('');
  const [visiable, setviable] = useState(false);
  console.log(principals);
  const handleClose = () => {
    if (inputValue === '') {
      setviable(false);
      return;
    }
    if (principals.filter(value => value === inputValue).length > 0) {
      setviable(false);
      return;
    }
    setTag(model => ({ ...model, principals: [...model.principals, inputValue] }));
    setviable(false);
  };
  return (
    <div>
      {principals.map((s: string, index: number) => {
        console.log(s, index);
        const longTag = s.length > 20;
        const tageElement = (
          <Tag
            key={s + index}
            closable={index >= 0}
            onClose={() => {
              setTag(model => ({
                ...model,
                principals: principals.filter(t => t !== s),
              }));
            }}
          >
            {longTag ? `${s.slice(0, 20)}...` : s}
          </Tag>
        );
        return tageElement;
      })}
      {visiable ? (
        <Input
          type="text"
          size="small"
          style={{ width: 78 }}
          value={inputValue}
          onBlur={handleClose}
          onPressEnter={handleClose}
          onChange={e => {
            setInputValue(e.target.value);
          }}
        />
      ) : (
        <Tag
          onClick={() => {
            setviable(true);
            setInputValue('');
          }}
        >
          <Icon type="plus" />
          New Tag
        </Tag>
      )}
    </div>
  );
};

export default X5Tag;
