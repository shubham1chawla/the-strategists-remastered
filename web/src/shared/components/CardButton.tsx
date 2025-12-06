import { KeyboardEvent, MouseEvent, ReactNode, useCallback } from 'react';
import { Card } from 'antd';

interface CardButtonProps {
  title: ReactNode;
  description: ReactNode;
  onClickOrEnter: (
    event: MouseEvent<HTMLDivElement> | KeyboardEvent<HTMLDivElement>,
  ) => void;
}

function CardButton({ title, description, onClickOrEnter }: CardButtonProps) {
  const onKeyUp = useCallback(
    (event: KeyboardEvent<HTMLDivElement>) => {
      if (event.key === 'Enter' || event.keyCode === 13) {
        onClickOrEnter(event);
      }
    },
    [onClickOrEnter],
  );
  return (
    <Card
      role="button"
      tabIndex={0}
      className="strategists-card-button"
      onKeyUp={onKeyUp}
      onClick={onClickOrEnter}
      hoverable
    >
      <Card.Meta title={title} description={description} />
    </Card>
  );
}

export default CardButton;
